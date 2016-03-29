package com.mygdx.game.actors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;


public interface Collidable {
    public void handleCollision(PooledEngine engine, Entity collider, Entity collidee);
//    public void handleSensorCollision(short categoryBits, boolean beginCollision);
}
