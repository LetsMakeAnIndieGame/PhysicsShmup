package com.mygdx.game.actors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

/**
 * Created by Phil on 1/17/2015.
 */
public interface Collidable {
    public void handleCollision(Engine engine, Entity collider, Entity collidee);
//    public void handleSensorCollision(short categoryBits, boolean beginCollision);
}
