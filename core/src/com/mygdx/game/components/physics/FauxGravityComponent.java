package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/21/2015.
 */
public class FauxGravityComponent implements Component {
    public float gravity = 0.0f;

    public FauxGravityComponent(float gravity) {
        this.gravity = gravity;
    }
}
