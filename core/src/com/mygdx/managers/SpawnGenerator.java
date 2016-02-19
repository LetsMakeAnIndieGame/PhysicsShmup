package com.mygdx.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    public static void spawnPlayer(World world, TiledMap tiledMap, Engine engine) {
        boolean hasPlayer = false;

        MapObjects objects = tiledMap.getLayers().get("SpawnPoints").getObjects();
        Iterator<MapObject> objectIterator = objects.iterator();

        while(objectIterator.hasNext()) {
            MapObject object = objectIterator.next();

            if (object.getProperties().get("toSpawn", String.class).equalsIgnoreCase("Player")) {
                hasPlayer = true;

                int spawnPointX = object.getProperties().get("x", float.class).intValue();
                int spawnPointY = object.getProperties().get("y", float.class).intValue();

                Entity player = new Entity();
                PlayerAgent playerAgent = new PlayerAgent(player);

                EntityManager.add(playerAgent);
                ShootingComponent shootingComponent   = new ShootingComponent();
                TypeComponent typeComponent       = new TypeComponent(PhysicsManager.COL_PLAYER);
                PlayerDataComponent playerData          = new PlayerDataComponent();
                PlayerCollisionComponent playerColCom        = new PlayerCollisionComponent();
                PositionComponent positionComponent   = new PositionComponent(spawnPointX, spawnPointY, RenderPriority.MID);
                SpriteComponent spriteComponent     = new SpriteComponent(new Texture("Entities/Actors/player.png"));
                VelocityComponent velocityComponent   = new VelocityComponent(3f, 0f);
                BodyComponent bodyComponent       = new BodyComponent(positionComponent,
                        BodyGenerator.generateBody(player,
                                spriteComponent.sprites.get(0),
                                Gdx.files.internal("Entities/BodyDefinitions/PlayerBody.json"),
                                PhysicsManager.FRIENDLY_BITS));
                LookAngleComponent lookAngleComponent  = new LookAngleComponent(90f);
                RenderableComponent renderableComponent = new RenderableComponent();
                SensorCollisionComponent sensorColComp  = new SensorCollisionComponent();
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

                Entity crosshair = new Entity();
                AttachedComponent attachedComponent  = new AttachedComponent(player);
                CrosshairComponent crosshairComponent = new CrosshairComponent();
                positionComponent                     = new PositionComponent(spawnPointX + 150, spawnPointY, RenderPriority.HIGH);
                spriteComponent                       = new SpriteComponent(new Texture("Entities/Scene2D/crosshair_a.png"));
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

    public static void spawnEnemies(World world, TiledMap tiledMap, Engine engine) {
        MapObjects objects = tiledMap.getLayers().get("SpawnPoints").getObjects();
        Iterator<MapObject> objectIterator = objects.iterator();

        while (objectIterator.hasNext()) {
            MapObject object = objectIterator.next();

            if (object.getProperties().get("toSpawn", String.class).equalsIgnoreCase("Basic")) {
                String steeringFile = object.getProperties().get("steering", String.class);
                int spawnPointX = object.getProperties().get("x", float.class).intValue();
                int spawnPointY = object.getProperties().get("y", float.class).intValue();

                Entity enemy = new Entity();

                EnemyCollisionComponent enemyColCom = new EnemyCollisionComponent();
                EnemyDataComponent enemyDataCom = new EnemyDataComponent();
                SensorCollisionComponent sensorColComp2 = new SensorCollisionComponent();
                RenderableComponent renderableComponent = new RenderableComponent();
                TypeComponent typeComponent = new TypeComponent(PhysicsManager.COL_ENEMY);
                PositionComponent positionComponent = new PositionComponent(spawnPointX, spawnPointY, RenderPriority.MID);
                VelocityComponent velocityComponent = new VelocityComponent(0f, 0f);
                SpriteComponent spriteComponent = new SpriteComponent(new Texture("Entities/Actors/bird.png"));
                BodyComponent bodyComponent = new BodyComponent(positionComponent,
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
                FlyingTestEnemyComponent enemyAgentComponent = new FlyingTestEnemyComponent(enemy, enemySteering);
                enemy.add(enemyAgentComponent);

                EntityManager.add(enemyAgentComponent);

                Entity healthBar = new Entity();
                AttachedComponent attachedComponent = new AttachedComponent(enemy);
                positionComponent = new PositionComponent(0, 0, RenderPriority.MID);
                HealthBarComponent healthBarComponent = new HealthBarComponent();
                spriteComponent = new SpriteComponent(new Texture("Entities/Actors/enemyhealthbg.png"), new Texture("Entities/Actors/enemyhealthfg.png"));

                healthBar.add(attachedComponent).add(positionComponent).add(spriteComponent).add(renderableComponent).add(healthBarComponent);

                engine.addEntity(enemy);
                engine.addEntity(healthBar);

                EntityManager.add(enemy);
                EntityManager.add(enemySteering);
            }
        }
    }
}
