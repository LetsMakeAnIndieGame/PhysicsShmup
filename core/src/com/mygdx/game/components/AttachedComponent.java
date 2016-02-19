package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;


public class AttachedComponent implements Component {
    public Entity attachedTo;

    public AttachedComponent(Entity attachedTo) {
        this.attachedTo = attachedTo;
    }
}
