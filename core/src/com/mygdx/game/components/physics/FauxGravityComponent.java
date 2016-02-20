package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;


public class FauxGravityComponent implements Component {
    public float gravity = 0.0f;

    public FauxGravityComponent(float gravity) {
        this.gravity = gravity;
    }
}
