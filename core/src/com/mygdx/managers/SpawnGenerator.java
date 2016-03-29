package com.mygdx.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.actors.*;
import com.mygdx.game.components.*;
import com.mygdx.game.components.collision.EnemyCollisionComponent;
import com.mygdx.game.components.collision.PlayerCollisionComponent;
import com.mygdx.game.components.collision.TypeComponent;
import com.mygdx.game.components.fsm.EnemyAgentComponent;
import com.mygdx.game.components.fsm.PlayerAgent;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.physics.VelocityComponent;
import com.mygdx.game.components.states.SensorCollisionComponent;

import java.util.Iterator;


public class SpawnGenerator {
    private static World world;
    private static TiledMap tiledMap;

    public static void spawnPlayer(World world, TiledMap tiledMap, PooledEngine engine) {
        boolean hasPlayer = false;

        MapObjects objects = tiledMap.getLayers().get("SpawnPoints").getObjects();
        Iterator<MapObject> objectIterator = objects.iterator();

        while(objectIterator.hasNext()) {
            MapObject object = objectIterator.next();

            if (object.getProperties().get("toSpawn", String.class).equalsIgnoreCase("Player")) {
                hasPlayer = true;

                int spawnPointX = object.getProperties().get("x", float.class).intValue();
                int spawnPointY = object.getProperties().get("y", float.class).intValue();

                Entity player = engine.createEntity();
                PlayerAgent playerAgent = new PlayerAgent(player);

                EntityManager.add(playerAgent);
                ShootingComponent shootingComponent     = engine.createComponent(ShootingComponent.class);

                TypeComponent typeComponent             = engine.createComponent(TypeComponent.class);
                typeComponent.type = PhysicsManager.COL_PLAYER;

                PlayerDataComponent playerData          = engine.createComponent(PlayerDataComponent.class);
                PlayerCollisionComponent playerColCom   = engine.createComponent(PlayerCollisionComponent.class);

                PositionComponent positionComponent     = engine.createComponent(PositionComponent.class);
                positionComponent.x = spawnPointX;
                positionComponent.y = spawnPointY;
                positionComponent.z = RenderPriority.MID;

                SpriteComponent spriteComponent         = engine.createComponent(SpriteComponent.class);
                spriteComponent.addTextures(new Texture("Entities/Actors/player.png"));

                VelocityComponent velocityComponent     = engine.createComponent(VelocityComponent.class);
                velocityComponent.x = 3f;
                velocityComponent.y = 0f;

                BodyComponent bodyComponent             = engine.createComponent(BodyComponent.class);
                bodyComponent.setBodyAndPosition(positionComponent,
                        BodyGenerator.generateBody(player,
                                spriteComponent.sprites.get(0),
                                Gdx.files.internal("Entities/BodyDefinitions/PlayerBody.json"),
                                PhysicsManager.FRIENDLY_BITS));

                LookAngleComponent lookAngleComponent   = engine.createComponent(LookAngleComponent.class);
                lookAngleComponent.angle = 90f;

                RenderableComponent renderableComponent = engine.createComponent(RenderableComponent.class);
                SensorCollisionComponent sensorColComp  = engine.createComponent(SensorCollisionComponent.class);
                player.add(positionComponent)
                        .add(velocityComponent)
                        .add(spriteComponent)
                        .add(bodyComponent)
                        .add(renderableComponent)
                        .add(lookAngleComponent)
                        .add(sensorColComp)
                        .add(shootingComponent)
                        .add(playerColCom)
                        .add(typeComponent)
                        .add(playerData);
                TestPlayerSteering playerSteering = new TestPlayerSteering(player);
                EntityManager.add(playerSteering);

                Entity crosshair = engine.createEntity();

                AttachedComponent attachedComponent   = engine.createComponent(AttachedComponent.class);
                attachedComponent.attachedTo = player;

                CrosshairComponent crosshairComponent = engine.createComponent(CrosshairComponent.class);

                positionComponent                     = engine.createComponent(PositionComponent.class);
                positionComponent.x = spawnPointX + 150;
                positionComponent.y = spawnPointY;
                positionComponent.z = RenderPriority.HIGH;

                spriteComponent                       = engine.createComponent(SpriteComponent.class);
                spriteComponent.addTextures(new Texture("Entities/Scene2D/crosshair_a.png"));
                crosshair.add(attachedComponent).add(positionComponent)
                        .add(spriteComponent).add(crosshairComponent)
                        .add(renderableComponent);

                engine.addEntity(player);
                engine.addEntity(crosshair);

                EntityManager.add(player);
                EntityManager.setPlayer(player);
                EntityManager.setPlayerSteering(playerSteering);

                break;
            }
        }

        if (!hasPlayer) {
            Gdx.app.log("Error", "Player spawn point is undefined in the level");
        }
    }

    public static void spawnEnemies(World world, TiledMap tiledMap, PooledEngine engine) {
        MapObjects objects = tiledMap.getLayers().get("SpawnPoints").getObjects();
        Iterator<MapObject> objectIterator = objects.iterator();

        while (objectIterator.hasNext()) {
            MapObject object = objectIterator.next();

            if (object.getProperties().get("toSpawn", String.class).equalsIgnoreCase("Basic")) {
                String steeringFile = object.getProperties().get("steering", String.class);
                int spawnPointX = object.getProperties().get("x", float.class).intValue();
                int spawnPointY = object.getProperties().get("y", float.class).intValue();

                Entity enemy = engine.createEntity();

                EnemyCollisionComponent enemyColCom     = engine.createComponent(EnemyCollisionComponent.class);
                EnemyDataComponent enemyDataCom         = engine.createComponent(EnemyDataComponent.class);
                SensorCollisionComponent sensorColComp2 = engine.createComponent(SensorCollisionComponent.class);
                RenderableComponent renderableComponent = engine.createComponent(RenderableComponent.class);

                TypeComponent typeComponent             = engine.createComponent(TypeComponent.class);
                typeComponent.type = PhysicsManager.COL_ENEMY;

                PositionComponent positionComponent     = engine.createComponent(PositionComponent.class);
                positionComponent.x = spawnPointX;
                positionComponent.y = spawnPointY;
                positionComponent.z = RenderPriority.MID;

                VelocityComponent velocityComponent     = engine.createComponent(VelocityComponent.class);
                velocityComponent.x = 0f;
                velocityComponent.y = 0f;

                SpriteComponent spriteComponent         = engine.createComponent(SpriteComponent.class);
                spriteComponent.addTextures(new Texture("Entities/Actors/bird.png"));

                BodyComponent bodyComponent             = engine.createComponent(BodyComponent.class);
                bodyComponent.setBodyAndPosition(positionComponent,
                        BodyGenerator.generateBody(enemy,
                                spriteComponent.sprites.get(0),
                                Gdx.files.internal("Entities/BodyDefinitions/EnemyBody.json"),
                                PhysicsManager.ENEMY_BITS));

                enemy.add(typeComponent).add(positionComponent).add(spriteComponent).add(bodyComponent)
                        .add(sensorColComp2).add(renderableComponent).add(enemyColCom).add(enemyDataCom)
                        .add(velocityComponent);
                Steering enemySteering = SteeringBuilder.createSteering(steeringFile+".json", enemy);
                enemySteering.setMaxLinearAcceleration(0.25f);
                enemySteering.setMaxLinearSpeed(2f);
                enemySteering.setMinLinearSpeed(0.0001f);
//                enemySteering.setTarget(EntityManager.getPlayerSteering());

                FlyingTestEnemyComponent enemyAgentComponent = engine.createComponent(FlyingTestEnemyComponent.class);
                enemyAgentComponent.construct(enemy, enemySteering);

                enemy.add(enemyAgentComponent);

                EntityManager.add(enemyAgentComponent);

                Entity healthBar = engine.createEntity();

                AttachedComponent attachedComponent = engine.createComponent(AttachedComponent.class);
                attachedComponent.attachedTo = enemy;

                positionComponent = engine.createComponent(PositionComponent.class);
                positionComponent.x = 0;
                positionComponent.y = 0;
                positionComponent.z = RenderPriority.MID;

                HealthBarComponent healthBarComponent = engine.createComponent(HealthBarComponent.class);

                spriteComponent = engine.createComponent(SpriteComponent.class);
                spriteComponent.addTextures(new Texture("Entities/Actors/enemyhealthbg.png"), new Texture("Entities/Actors/enemyhealthfg.png"));

                healthBar.add(attachedComponent).add(positionComponent).add(spriteComponent).add(renderableComponent).add(healthBarComponent);

                engine.addEntity(enemy);
                engine.addEntity(healthBar);

                EntityManager.add(enemy);
                EntityManager.add(enemySteering);
            }
        }
    }
}
