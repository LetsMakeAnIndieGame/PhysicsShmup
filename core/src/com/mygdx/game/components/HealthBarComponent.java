package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HealthBarComponent implements Component, Poolable {
    // this class should contain no data

    public HealthBarComponent() {}

    @Override
    public void reset() {
        // do nothing
    }
}
