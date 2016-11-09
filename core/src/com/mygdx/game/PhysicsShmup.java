package com.mygdx.game;


import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.pathfinding.PathfindingDebugger;
import com.mygdx.game.systems.CollisionManager;
import com.mygdx.managers.*;


public class PhysicsShmup extends ApplicationAdapter {
	public static long currentTimeMillis;

	private SpriteBatch batch;
	private GameInput input;

	float width;
	float height;
	OrthographicCamera camera;
	OrthogonalTiledMapRenderer tiledMapRenderer;

	World world;
	Box2DDebugRenderer debugRenderer;

	private EntityManager entityManager;

	public static InputMultiplexer inputMultiplexer = new InputMultiplexer();

	public static ShaderProgram createShader(String vert, String frag) {
		ShaderProgram prog = new ShaderProgram(vert, frag);
		if (!prog.isCompiled())
			throw new GdxRuntimeException("could not compile shader: " + prog.getLog());
		if (prog.getLog().length() != 0)
			Gdx.app.log("GpuShadows", prog.getLog());
		return prog;
	}

	void renderLight() {
		float mx = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + GameInput.getMousePos().x, width / 2), LevelManager.lvlPixelWidth - (width / 2)) + 25;
		float my = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y - GameInput.getMousePos().y, height / 2), LevelManager.lvlPixelHeight - (height / 2)) - 25;

		//STEP 1. render light region to occluder FBO

		//bind the occluder FBO
		occludersFBO.begin();

		//clear the FBO
		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//set the orthographic camera to the size of our FBO
		camera.setToOrtho(false, occludersFBO.getWidth(), occludersFBO.getHeight());

		//translate camera so that light is in the center
//		camera.translate(mx - lightSize/2f, my - lightSize/2f);
		camera.translate(mx, my);

		//update camera matrices
		camera.update();

		//set up our batch for the occluder pass
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(null); //use default shader
		batch.begin();

		// ... draw any sprites that will cast shadows here ... //
		// already taken care of elsewhere

		//end the batch before unbinding the FBO
		batch.end();

		//unbind the FBO
		occludersFBO.end();

		//STEP 2. build a 1D shadow map from occlude FBO

		//bind shadow map
		shadowMapFBO.begin();

		//clear it
		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//set our shadow map shader
		batch.setShader(shadowMapShader);
		batch.begin();
		shadowMapShader.setUniformf("resolution", lightSize, lightSize);
		shadowMapShader.setUniformf("upScale", upScale);

		//reset our projection matrix to the FBO size
		camera.setToOrtho(false, shadowMapFBO.getWidth(), shadowMapFBO.getHeight());
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		//draw the occluders texture to our 1D shadow map FBO
		batch.draw(bufferTexture.getTexture(), 0, 0, lightSize, shadowMapFBO.getHeight());

		//flush batch
		batch.end();

		//unbind shadow map FBO
		shadowMapFBO.end();

		//STEP 3. render the blurred shadows

		//reset projection matrix to screen
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + GameInput.getMousePos().x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y - GameInput.getMousePos().y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

//		// TEST DRAW THE SHADOW MAP!
//		batch.setShader(null);
//		batch.begin();
////		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
////		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ALWAYS);
//		batch.draw(shadowMapFBO.getColorBufferTexture(), 640, 360, 256, 50);
//		batch.end();

		shadowRenderShader.setUniformf("resolution", lightSize, lightSize);
		shadowRenderShader.setUniformf("softShadows", softShadows ? 1f : 0f);
		//set color to light
		batch.setColor(Color.WHITE);

		float finalSize = lightSize * upScale;

//		batch.enableBlending();

		//draw centered on light position
//		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
		FrameBuffer shadowBlendFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		TextureRegion shadowBlendTex = new TextureRegion(shadowMapFBO.getColorBufferTexture());
		shadowBlendTex.flip(false, true);

		shadowBlendFBO.begin();

		//set the shader which actually draws the light/shadow
		batch.setShader(shadowRenderShader);
		batch.begin();
		// Clear the buffer before drawing light to it
		Gdx.gl.glClearColor(0f,0f,0f,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(true); // reset the ortho so that it draws properly.
		camera.update();
		batch.setProjectionMatrix(camera.combined);
//		batch.draw(shadowMap1D.getTexture(), mx-finalSize/2f, my-finalSize/2f, finalSize, finalSize);
//		batch.draw(shadowMap1D.getTexture(), shadowBlendTex.getRegionWidth() / 2, shadowBlendTex.getRegionHeight() / 2, finalSize, finalSize);
		shadowBlendFBO.end();

		batch.setShader(null);

		camera.setToOrtho(false);
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + GameInput.getMousePos().x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y - GameInput.getMousePos().y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
		batch.draw(shadowBlendTex.getTexture(), mx-finalSize/2f, my-finalSize/2f);
//		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		//flush the batch before swapping shaders
		batch.end();

		//reset color
		batch.setColor(Color.WHITE);

		camera.setToOrtho(false);
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + GameInput.getMousePos().x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y - GameInput.getMousePos().y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
	}

	private void renderFBO() {
		frameBuffer.begin();

		Gdx.gl.glClearColor(0f,0f,0f,0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		camera.setToOrtho(true, frameBuffer.getWidth(), frameBuffer.getHeight());
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x + 25, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y + 25, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();

//		batch.setProjectionMatrix(camera.combined);

		tiledMapRenderer.setView(camera);

		int[] fgLayer = {1};
		tiledMapRenderer.render(fgLayer);

		frameBuffer.end();
		batch.end();

		camera.setToOrtho(false);
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
	}

	private int lightSize = 1028; // 256

	private float upScale = 1f; //for example; try lightSize=128, upScale=1.5f

	TextureRegion shadowMap1D; //1 dimensional shadow map
	TextureRegion occluders;   //occluder map

	FrameBuffer shadowMapFBO;
	FrameBuffer occludersFBO;

	FrameBuffer frameBuffer;
	TextureRegion bufferTexture;

	Texture casterSprites;
	Texture light;

	ShaderProgram shadowMapShader, shadowRenderShader, levelRenderShader;

	boolean additive = true;
	boolean softShadows = true;

	@Override
	public void create () {
		/*
			Lighting shader stuff
		 */
		ShaderProgram.pedantic = false;

		//read vertex pass-through shader
		final String VERT_SRC = Gdx.files.internal("Shaders/pass.vert").readString();
		final String LEVEL_VERT_SRC = Gdx.files.internal("Shaders/levelpass.vert").readString();
		final String SHADOW_VERT_SRC = Gdx.files.internal("Shaders/shadowpass.vert").readString();
		final String FRAG_SRC = Gdx.files.internal("Shaders/pass.frag").readString();

		// renders occluders to 1D shadow map
		shadowMapShader = createShader(VERT_SRC, Gdx.files.internal("Shaders/shadowMap.frag").readString());
		// samples 1D shadow map to create the blurred soft shadow
		shadowRenderShader = createShader(SHADOW_VERT_SRC, Gdx.files.internal("Shaders/shadowRender.frag").readString());
		// renders dimmed ambient light
		levelRenderShader = createShader(LEVEL_VERT_SRC, FRAG_SRC);

		//build frame buffers
		occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
		occluders = new TextureRegion(occludersFBO.getColorBufferTexture());
		occluders.flip(false, true);

		//our 1D shadow map, lightSize x 1 pixels, no depth
		shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, 1, false);
		Texture shadowMapTex = shadowMapFBO.getColorBufferTexture();

		//use linear filtering and repeat wrap mode when sampling
		shadowMapTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		shadowMapTex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		//for debugging only; in order to render the 1D shadow map FBO to screen
		shadowMap1D = new TextureRegion(shadowMapTex);
//		shadowMap1D.flip(false, true);

//		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, false);
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, lightSize, lightSize, false);
		bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
		bufferTexture.flip(false, true);
		/*
			End lighting shader stuff
		 */

        LevelManager.loadLevel("Levels/testlvl.tmx");
//		Gdx.input.setCursorCatched(true); // remove mouse cursor
		PooledEngine engine = new PooledEngine(50, 100, 100, 150);

		input = new GameInput();
		inputMultiplexer.addProcessor(input);
		Gdx.input.setInputProcessor(inputMultiplexer);

//		SoundManager.playMusic("Music/PossibleBossFight.ogg", 0.2f);

		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();
		world = new World(new Vector2(0f, -9.8f), false);

		entityManager = new EntityManager(world, engine, batch);
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false);
//		camera.update();

        SoundManager.setCamera(camera);
        PathfindingDebugger.setCamera(camera);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(LevelManager.tiledMap);

        LevelCollisionGenerator lcg = new LevelCollisionGenerator(world);
        lcg.createPhysics(LevelManager.tiledMap);

//		TextManager.SetSpriteBatch(batch, camera);

		Lighting.createLights(world, LevelManager.tiledMap);

		UIManager.createHUD();

        new CollisionManager(engine, world);

        SpawnGenerator.spawnPlayer(world, LevelManager.tiledMap, engine);
        SpawnGenerator.spawnEnemies(world, LevelManager.tiledMap, engine);
	}

	@Override
	public void render () {
//        Gdx.app.log("FPS", "" + Gdx.graphics.getFramesPerSecond() + " " + Gdx.graphics.getDeltaTime());

		currentTimeMillis = System.currentTimeMillis();

        MessageManager.getInstance().update();
		world.step(1f / 60f, 6, 2);

		Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PhysicsManager.METERS_TO_PIXELS, PhysicsManager.METERS_TO_PIXELS, 0);

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//		EntityManager.update(world);
		GameInput.staticUpdate();
		Time.update();

		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		tiledMapRenderer.getBatch().setShader(levelRenderShader);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		tiledMapRenderer.getBatch().setShader(null);

		renderFBO();

		// Debug render of occluders
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
//		batch.setShader(levelRenderShader);
		batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
		batch.draw(new Texture("Entities/Actors/bird.png"), EntityManager.getPlayer().getComponent(PositionComponent.class).x - 550, EntityManager.getPlayer().getComponent(PositionComponent.class).y - 2);
		batch.setShader(null);
		batch.end();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		//clear frame
//		Gdx.gl.glClearColor(0.25f,0.25f,0.25f,1f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		entityManager.update(); // this may draw entities hence batch.begin() {...} batch.end() // TODO: Resolve this issue
        EntityManager.update(world); // static update for adding and removing entities
        // Do this update last or you'll have problems
        input.update();
//		batch.setProjectionMatrix(camera.combined);
//		EntityManager.draw(batch);
		batch.end();

//		Lighting.updateAndShowLights(camera);

//		debugRenderer.render(world, debugMatrix);
		UIManager.update();
		UIManager.render();
//		TextManager.draw("FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);

		/*
			Lighting shader stuff starts
		 */
//		//clear frame
//		Gdx.gl.glClearColor(0.25f,0.25f,0.25f,1f);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		float mx = Gdx.input.getX();
//		float my = Gdx.graphics.getHeight() - Gdx.input.getY();

		if (additive)
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

		renderLight();

		if (additive)
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		//STEP 4. render sprites in full colour
		batch.begin();
		batch.setShader(null); //default shader
//		batch.setColor(Color.CYAN);
//		batch.draw(bufferTexture.getTexture(), 0, 0);

		//DEBUG RENDERING -- show occluder map and 1D shadow map
		batch.setColor(Color.RED);
//		batch.draw(occluders, Gdx.graphics.getWidth()-lightSize, 0);
		batch.draw(occluders,0,0);
//		batch.draw(shadowMap1D, Gdx.graphics.getWidth()-lightSize, lightSize+5);
		batch.draw(shadowMap1D, 0, 0);
		batch.setColor(Color.WHITE);

//		//DEBUG RENDERING -- show light
//		batch.draw(light, mx-light.getWidth()/2f, my-light.getHeight()/2f); //mouse
//		batch.draw(light, Gdx.graphics.getWidth()-lightSize/2f-light.getWidth()/2f, lightSize/2f-light.getHeight()/2f);
		batch.end();
		/*
			Lighting shader stuff ends
		 */

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();

		if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
//			Gdx.graphics.setWindowedMode(1920, 1080);
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}

		if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
			camera.zoom += 0.01f;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.PLUS)) {
			camera.zoom -= 0.01f;
		}
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		camera.setToOrtho(false, width, height);
		camera.update();
	}

	@Override
	public void dispose() {
		world.dispose();
		SoundManager.dispose();
		batch.dispose();
		Lighting.destroyLights();
	}
}
