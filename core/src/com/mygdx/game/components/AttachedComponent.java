package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
/**
 * Created by Phil on 2/9/2015.
 */
public class AttachedComponent implements Component {
    public Entity attachedTo;

    public AttachedComponent(Entity attachedTo) {
        this.attachedTo = attachedTo;
    }
}
