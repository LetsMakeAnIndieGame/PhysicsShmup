package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.managers.PhysicsManager;

<<<<<<< HEAD

=======
/**
 * Created by Phil on 2/8/2015.
 */
>>>>>>> 437872d6f8d44f9dc3ffe938a1dca805f6282a1d
public class BodyComponent implements Component {
    public Body body;

    public BodyComponent(PositionComponent positionCom, Body body) {
        this.body = body;
        this.body.setTransform(positionCom.x * PhysicsManager.PIXELS_TO_METERS, positionCom.y * PhysicsManager.PIXELS_TO_METERS, 0);
    }
}
