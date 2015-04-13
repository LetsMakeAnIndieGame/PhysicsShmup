package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
* Created by Phil on 2/4/2015.
*/
public class VelocityComponent extends Component {
    public float x = 0;
    public float y = 0;

    public VelocityComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
