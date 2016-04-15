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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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

	private FrameBuffer frameBuffer;
	private TextureRegion bufferTexture;

	@Override
	public void create () {
        LevelManager.loadLevel("Levels/testlvl.tmx");
		Gdx.input.setCursorCatched(true); // remove mouse cursor
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

		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, false);
		bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
	}

	private void renderFBO() {
		frameBuffer.begin();

		Gdx.gl.glClearColor(0f,0f,0f,1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.setColor(Color.CYAN);

		camera.setToOrtho(true);
		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		frameBuffer.end();
		batch.end();

		camera.setToOrtho(false);
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

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		renderFBO();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bufferTexture.getTexture(), 50, 50, 444, 250);
		batch.end();

		camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
		camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
		camera.update();
		batch.setProjectionMatrix(camera.combined);

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

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
			Gdx.app.exit();

		if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
//			Gdx.graphics.setWindowedMode(1920, 1080);
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
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
