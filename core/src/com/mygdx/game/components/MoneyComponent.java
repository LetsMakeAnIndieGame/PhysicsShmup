package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MoneyComponent implements Component, Poolable {
    public int value = 100;

    public MoneyComponent() {}

    public MoneyComponent(int value) { this.value = value; }

    @Override
    public void reset() {
        value = 100;
    }
}
