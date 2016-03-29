package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderableComponent implements Component, Poolable {
    // this class should have no data
    public RenderableComponent() {}

    @Override
    public void reset() {
        // do nothing
    }
}
