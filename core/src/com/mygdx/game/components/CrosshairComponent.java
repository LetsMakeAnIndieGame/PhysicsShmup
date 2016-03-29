package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CrosshairComponent implements Component, Poolable {
    // this class should have no data

    public CrosshairComponent() {}

    @Override
    public void reset() {
        // do nothing
    }
}
