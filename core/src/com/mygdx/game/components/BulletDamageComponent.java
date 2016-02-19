package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/19/2015.
 */
public class BulletDamageComponent implements Component {
    public long damage = 10;

    public BulletDamageComponent(long damage) {
        this.damage = damage;
    }
}
