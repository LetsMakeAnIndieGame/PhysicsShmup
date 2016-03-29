package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PositionComponent implements Component, Poolable {
    public float x = 0.0f;
    public float y = 0.0f;
    public int z = 0;

    public PositionComponent() {}

    public PositionComponent(float x, float y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void reset() {
        x = 0.0f;
        y = 0.0f;
        z = 0;
    }
}
