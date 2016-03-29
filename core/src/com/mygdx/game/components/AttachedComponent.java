package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AttachedComponent implements Component, Poolable {
    public Entity attachedTo;

    public AttachedComponent() {}

    public AttachedComponent(Entity attachedTo) {
        this.attachedTo = attachedTo;
    }

    @Override
    public void reset() {
        this.attachedTo = null;
    }
}
