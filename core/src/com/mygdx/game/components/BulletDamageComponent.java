package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletDamageComponent implements Component, Poolable {
    public long damage = 10;

    public BulletDamageComponent() {}

    public BulletDamageComponent(long damage) {
        this.damage = damage;
    }

    @Override
    public void reset() {
        damage = 10;
    }
}
