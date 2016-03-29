package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;


public class VelocityComponent implements Component, Poolable {
    public float x = 0;
    public float y = 0;

    public VelocityComponent() {}

    public VelocityComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void reset() {
        x = 0;
        y = 0;
    }
}
