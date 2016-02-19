package com.mygdx.game.components.graphics;

import com.badlogic.ashley.core.Component;


public class TransparentComponent implements Component {
    public float transparency = 1;

    public TransparentComponent(float transparency) {
        this.transparency = transparency;
    }
}
