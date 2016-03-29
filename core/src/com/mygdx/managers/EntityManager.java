package com.mygdx.managers;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.PhysicsShmup;
import com.mygdx.game.actors.*;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.graphics.TransparentComponent;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.Time;
import com.mygdx.game.systems.*;


public class EntityManager {
    private static PooledEngine engine;
    private static Entity player;
    private World world;
    private static TestPlayerSteering playerSteering;

    public static void setPlayerSteering(TestPlayerSteering steering) {
        playerSteering = steering;
    }

    public static TestPlayerSteering getPlayerSteering() {
        return playerSteering;
    }

    public static void setPlayer(Entity entity) {
        player = entity;
    }

    public static Entity getPlayer() {
        return player;
    }

    public static void createBulletHole(Vector2 position, Vector2 normal) {
        Entity bulletHole = new Entity();

        SpriteComponent spriteComponent = engine.createComponent(SpriteComponent.class);
        spriteComponent.addTextures(new Texture("Entities/Actors/bullethole.png"));

        Sprite theSprite = spriteComponent.sprites.get(0);
        float offsetX = theSprite.getWidth() / 2 + normal.x * theSprite.getWidth() / 2;
        float offsetY = theSprite.getHeight() / 2 + normal.y * theSprite.getHeight() / 2;

        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.x = position.x * PhysicsManager.METERS_TO_PIXELS - offsetX;
        positionComponent.y = position.y * PhysicsManager.METERS_TO_PIXELS - offsetY;
        positionComponent.z = RenderPriority.LOW;

        RenderableComponent renderableComponent = engine.createComponent(RenderableComponent.class);

        DeathTimerComponent deathTimerComponent = engine.createComponent(DeathTimerComponent.class);
        deathTimerComponent.createTime = PhysicsShmup.currentTimeMillis;
        deathTimerComponent.deathTime = 20000;

        TransparentComponent transparentComponent = engine.createComponent(TransparentComponent.class);
        transparentComponent.transparency = 0;

        bulletHole.add(positionComponent).add(renderableComponent).add(spriteComponent).add(deathTimerComponent).add(transparentComponent);

        engine.addEntity(bulletHole);
        EntityManager.add(bulletHole);
    }

    public EntityManager(World world, PooledEngine e, SpriteBatch batch) {
        engine = e;
        this.world = world;

        BodyGenerator.registerWorld(world);

        AimingSystem aimingSystem            = new AimingSystem();
        BitmapFontRenderSystem bitmapFontRenderSystem  = new BitmapFontRenderSystem(batch);
        CollisionManager collisionSystem         = new CollisionManager(engine, world);
        CrosshairSystem crosshairSystem         = new CrosshairSystem();
        DeathTimerSystem        deathTimerSystem        = new DeathTimerSystem();
        FauxGravitySystem       fauxGravitySystem       = new FauxGravitySystem();
        HealthBarSystem         healthBarSystem         = new HealthBarSystem();
        PositionSystem          positionSystem          = new PositionSystem();
        ShootingSystem          shootingSystem          = new ShootingSystem(engine, world);
        SpriteRenderSystem      spriteRenderSystem      = new SpriteRenderSystem(batch);
        TurnaroundSystem        turnaroundSystem        = new TurnaroundSystem();

        engine.addSystem(spriteRenderSystem); engine.addSystem(crosshairSystem);
        engine.addSystem(aimingSystem); engine.addSystem(positionSystem);
        engine.addSystem(shootingSystem); engine.addSystem(turnaroundSystem);
        engine.addSystem(bitmapFontRenderSystem); engine.addSystem(deathTimerSystem);
        engine.addSystem(fauxGravitySystem); engine.addSystem(healthBarSystem);
    }

    public void update() {
        engine.update((float) Time.time);
        for (Updateable updateable : updateables) {
            updateable.update(0f);
        }

        for (Entity entity : destroyEntities) {
            try {
                world.destroyBody(entity.getComponent(BodyComponent.class).body);
            } catch(Exception e) {
            //do nothing
            }
            engine.removeEntity(entity);
        }
        destroyEntities.clear();
    }

    private static Array<Entity> entities = new Array<Entity>();
    private static Array<Entity> destroyEntities = new Array<Entity>();
    private static Array<Updateable> updateables = new Array<Updateable>();
    // make a destroyUpdateables array so properly clean the references

    public static void add(Entity entity) {
        entities.add(entity);
    }
    public static void add(Updateable agent) { updateables.add(agent); }

    public static void setToDestroy(Entity entity) {
        entities.removeValue(entity, true);
        destroyEntities.add(entity);
    }

    public static void update(World world) {
        for (Entity entity : destroyEntities) {
            world.destroyBody(entity.getComponent(BodyComponent.class).body);
        }
        destroyEntities.clear();
    }
}
