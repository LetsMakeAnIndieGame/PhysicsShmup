package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.components.DeathTimerComponent;
import com.mygdx.game.components.collision.*;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.graphics.TransparentComponent;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.states.*;
import com.mygdx.managers.EntityManager;
import com.mygdx.managers.GameInput;
import com.mygdx.managers.PhysicsManager;
import com.mygdx.managers.RenderPriority;

/**
 * Created by Phil on 2/10/2015.
 */
public class CollisionManager implements ContactListener {
    private Engine engine;

    private ComponentMapper<SensorCollisionComponent>   sensorColMap    = ComponentMapper.getFor(SensorCollisionComponent.class);
    private ComponentMapper<TypeComponent>              typeMap         = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<PlayerCollisionComponent>   playerColMap    = ComponentMapper.getFor(PlayerCollisionComponent.class);
    private ComponentMapper<MoneyCollisionComponent>    moneyColMap     = ComponentMapper.getFor(MoneyCollisionComponent.class);
    private ComponentMapper<BulletCollisionComponent>   bulletColMap    = ComponentMapper.getFor(BulletCollisionComponent.class);
    private ComponentMapper<EnemyCollisionComponent>    enemyColMap     = ComponentMapper.getFor(EnemyCollisionComponent.class);

    private void createCollision(Entity a, Entity b) {
        short typeA;
        short typeB;

        try {
            typeA = typeMap.get(a).type;
            typeB = typeMap.get(b).type;
        } catch (Exception e) {
            // If one of the objects doesn't have a type, then it's not a useful collision
            return;
        }

        if (typeA == PhysicsManager.COL_PLAYER)
            playerColMap.get(a).handleCollision(engine, a, b);
        else if (typeA == PhysicsManager.COL_MONEY)
            moneyColMap.get(a).handleCollision(engine, a, b);
        else if (typeA == PhysicsManager.COL_FRIENDLY_BULLET)
            bulletColMap.get(a).handleCollision(engine, a, b);
        else if (typeA == PhysicsManager.COL_ENEMY)
            enemyColMap.get(a).handleCollision(engine, a, b);

        if (typeB == PhysicsManager.COL_PLAYER)
            playerColMap.get(b).handleCollision(engine, b, a);
        else if (typeB == PhysicsManager.COL_MONEY)
            moneyColMap.get(b).handleCollision(engine, b, a);
        else if (typeB == PhysicsManager.COL_FRIENDLY_BULLET)
            bulletColMap.get(b).handleCollision(engine, b, a);
        else if (typeB == PhysicsManager.COL_ENEMY)
            enemyColMap.get(b).handleCollision(engine, b, a);
    }

    public CollisionManager(Engine engine, World world) {
        this.engine = engine;
        world.setContactListener(this);
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Vector2 position;
        Vector2 normal = contact.getWorldManifold().getNormal();

        if ((fixtureA.getBody().isBullet() && fixtureB.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS) ||
                (fixtureB.getBody().isBullet() && fixtureA.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS)) {

            position = contact.getWorldManifold().getPoints()[0];
            EntityManager.createBulletHole(position, normal);
        }

        Entity actorA = (Entity) fixtureA.getUserData();
        Entity actorB = (Entity) fixtureB.getUserData();

        createCollision(actorA, actorB);

        SensorCollisionComponent data;
        short categoryBits;

        if (fixtureA.isSensor() && fixtureB.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS) {
            data = sensorColMap.get(actorA);
            categoryBits = fixtureA.getFilterData().categoryBits;
        } else if (fixtureB.isSensor() && fixtureA.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS) {
            data = sensorColMap.get(actorB);
            categoryBits = fixtureB.getFilterData().categoryBits;
        } else {
            return;
        }

        switch (categoryBits) {
            case PhysicsManager.FOOT_SENSOR:
                data.numFoot++;
                break;
            case PhysicsManager.RIGHT_WALL_SENSOR:
                data.numRightWall++;
                break;
            case PhysicsManager.LEFT_WALL_SENSOR:
                data.numLeftWall++;
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Entity actorA = (Entity) fixtureA.getUserData();
        Entity actorB = (Entity) fixtureB.getUserData();

        SensorCollisionComponent data;
        short categoryBits;

        if (fixtureA.isSensor() && fixtureB.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS) {
            data = sensorColMap.get(actorA);
            categoryBits = fixtureA.getFilterData().categoryBits;
        } else if (fixtureB.isSensor() && fixtureA.getFilterData().categoryBits == PhysicsManager.LEVEL_BITS) {
            data = sensorColMap.get(actorB);
            categoryBits = fixtureB.getFilterData().categoryBits;
        } else {
            return;
        }

        switch (categoryBits) {
            case PhysicsManager.FOOT_SENSOR:
                data.numFoot--;
                break;
            case PhysicsManager.RIGHT_WALL_SENSOR:
                data.numRightWall--;
                break;
            case PhysicsManager.LEFT_WALL_SENSOR:
                data.numLeftWall--;
                break;
        }
    }
}