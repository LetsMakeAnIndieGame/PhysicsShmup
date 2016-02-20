package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.actors.Collidable;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.managers.EntityManager;


public class BulletCollisionComponent implements Component, Collidable {
    @Override
    public void handleCollision(Engine engine, Entity collider, Entity collidee) {
        // Bullets will always be destroyed when handling a collision?
        collider.getComponent(BodyComponent.class).body.getFixtureList().first().setUserData(null); // <= is this a good solution?
        collider.remove(RenderableComponent.class);
        EntityManager.setToDestroy(collider);
    }
}
