package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class MoneyComponent implements Component {
    public int value = 100;

    public MoneyComponent() {}

    public MoneyComponent(int value) { this.value = value; }
}
