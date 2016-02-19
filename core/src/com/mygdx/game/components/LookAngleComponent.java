package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;


public class LookAngleComponent implements Component {
    public float angle = 0.0f;

    public LookAngleComponent(float angle) {
        this.angle = angle;
    }
}
