package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/16/2015.
 */
public class TypeComponent implements Component {
    public short type;

    public TypeComponent(short type) {
        this.type = type;
    }
}
