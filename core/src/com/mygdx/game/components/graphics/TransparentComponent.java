package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/21/2015.
 */
public class TransparentComponent extends Component {
    public float transparency = 1;

    public TransparentComponent(float transparency) {
        this.transparency = transparency;
    }
}
