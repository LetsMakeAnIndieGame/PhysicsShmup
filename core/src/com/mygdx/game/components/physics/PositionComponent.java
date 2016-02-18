package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;

/**
* Created by Phil on 2/4/2015.
*/
public class PositionComponent implements Component {
    public float x = 0.0f;
    public float y = 0.0f;
    public int z = 0;

    public PositionComponent(float x, float y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
