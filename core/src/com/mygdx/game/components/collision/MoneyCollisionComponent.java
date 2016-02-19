package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.mygdx.game.actors.Collidable;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.managers.EntityManager;
import com.mygdx.managers.PhysicsManager;

<<<<<<< HEAD
=======
/**
 * Created by Phil on 2/16/2015.
 */
>>>>>>> 437872d6f8d44f9dc3ffe938a1dca805f6282a1d
public class MoneyCollisionComponent implements Component, Collidable {
    @Override
    public void handleCollision(Engine engine, Entity collider, Entity collidee) {
        short type = collidee.getComponent(TypeComponent.class).type;

        if (type == PhysicsManager.COL_PLAYER) {
            collider.remove(RenderableComponent.class);
            EntityManager.setToDestroy(collider); // to destroy the body and remove references to this entity
        }
    }
}
