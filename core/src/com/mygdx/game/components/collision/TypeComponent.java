package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TypeComponent implements Component, Poolable {
    public short type;

    public TypeComponent() {}

    public TypeComponent(short type) {
        this.type = type;
    }

    @Override
    public void reset() {
        type = 0;
    }
}
