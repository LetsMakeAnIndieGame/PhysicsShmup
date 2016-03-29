package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;


public class FauxGravityComponent implements Component, Poolable {
    public float gravity = 0.0f;

    public FauxGravityComponent() {}

    public FauxGravityComponent(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void reset() {
        gravity = 0.0f;
    }
}
