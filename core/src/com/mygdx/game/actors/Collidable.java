package com.mygdx.game.actors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;


public interface Collidable {
    public void handleCollision(Engine engine, Entity collider, Entity collidee);
//    public void handleSensorCollision(short categoryBits, boolean beginCollision);
}
