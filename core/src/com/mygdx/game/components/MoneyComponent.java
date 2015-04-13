package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/16/2015.
 */
public class MoneyComponent extends Component {
    public int value = 100;

    public MoneyComponent() {}

    public MoneyComponent(int value) { this.value = value; }
}
