package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/9/2015.
 */
public class LookAngleComponent extends Component {
    public float angle = 0.0f;

    public LookAngleComponent(float angle) {
        this.angle = angle;
    }
}
