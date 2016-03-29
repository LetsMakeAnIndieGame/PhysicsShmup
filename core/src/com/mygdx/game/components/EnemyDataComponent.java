package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EnemyDataComponent implements Component, Poolable {
    public long level = 1;
    public long health = 100;
    public long maxHealth = 100;
    public long shield = 0;
    public long maxShield = 0;

    public EnemyDataComponent() {}

    @Override
    public void reset() {
        level = 1;
        health = 100;
        maxHealth = 100;
        shield = 0;
        maxShield = 0;
    }
}
