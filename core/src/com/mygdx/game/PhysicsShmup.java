package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.actors.ThreadTest;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.pathfinding.GraphGenerator;
import com.mygdx.game.pathfinding.HeuristicImp;
import com.mygdx.game.pathfinding.Node;
import com.mygdx.game.pathfinding.PathfindingDebugger;
import com.mygdx.game.systems.CollisionManager;
import com.mygdx.managers.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PhysicsShmup extends ApplicationAdapter {
	public static long currentTimeMillis;

	private SpriteBatch batch;
	private GameInput input;

	float width;
	float height;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;

	World world;
	Box2DDebugRenderer debugRenderer;

	private EntityManager entityManager;

	public static InputMultiplexer inputMultiplexer = new InputMultiplexer();

	@Override
	public void create () {
        ThreadPoolExecutor execServ = (ThreadPoolExecutor) Executors.newCachedThreadPool();
//        execServ.`
        LevelManager.loadLevel("Levels/testlvl.tmx");
		Gdx.input.setCursorCatched(true); // remove mouse cursor
		Engine engine = new Engine();

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
		camera.setToOrtho(false, width, height);
		camera.update();

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
        Gdx.app.log("FPS", "" + Gdx.graphics.getFramesPerSecond());
		currentTimeMillis = System.currentTimeMillis();

        MessageManager.getInstance().update(Gdx.graphics.getRawDeltaTime());
		world.step(1f / 60f, 6, 2);

		Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PhysicsManager.METERS_TO_PIXELS, PhysicsManager.METERS_TO_PIXELS, 0);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//		EntityManager.update(world);
		GameInput.staticUpdate();
		Time.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

        camera.position.x = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).x, width / 2), LevelManager.lvlPixelWidth - (width / 2));
        camera.position.y = Math.min(Math.max(EntityManager.getPlayer().getComponent(PositionComponent.class).y, height / 2), LevelManager.lvlPixelHeight - (height / 2));
        camera.update();

		batch.begin();
		entityManager.update(); // this may draw entities hence batch.begin() {...} batch.end()
        EntityManager.update(world); // static update for adding and removing entities
        // Do this update last or you'll have problems
        input.update();
		batch.setProjectionMatrix(camera.combined);
//		EntityManager.draw(batch);
		batch.end();

		Lighting.updateAndShowLights(camera);

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
