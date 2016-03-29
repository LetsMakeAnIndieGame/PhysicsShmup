package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;


public class LookAngleComponent implements Component, Poolable {
    public float angle = 0.0f;

    public LookAngleComponent() {}

    public LookAngleComponent(float angle) {
        this.angle = angle;
    }

    @Override
    public void reset() {
        angle = 0.0f;
    }
}
