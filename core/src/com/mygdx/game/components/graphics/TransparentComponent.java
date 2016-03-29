package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TransparentComponent implements Component, Poolable {
    public float transparency = 1;

    public TransparentComponent() {}

    public TransparentComponent(float transparency) {
        this.transparency = transparency;
    }

    @Override
    public void reset() {
        transparency = 1;
    }
}
