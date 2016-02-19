package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
<<<<<<< HEAD


=======
/**
 * Created by Phil on 2/9/2015.
 */
>>>>>>> 437872d6f8d44f9dc3ffe938a1dca805f6282a1d
public class AttachedComponent implements Component {
    public Entity attachedTo;

    public AttachedComponent(Entity attachedTo) {
        this.attachedTo = attachedTo;
    }
}
