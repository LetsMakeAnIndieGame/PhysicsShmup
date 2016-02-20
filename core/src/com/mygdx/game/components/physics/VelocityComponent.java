package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;


public class VelocityComponent implements Component {
    public float x = 0;
    public float y = 0;

    public VelocityComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
