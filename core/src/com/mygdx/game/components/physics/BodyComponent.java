package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.managers.PhysicsManager;

/**
 * Created by Phil on 2/8/2015.
 */
public class BodyComponent extends Component {
    public Body body;

    public BodyComponent(PositionComponent positionCom, Body body) {
        this.body = body;
        this.body.setTransform(positionCom.x * PhysicsManager.PIXELS_TO_METERS, positionCom.y * PhysicsManager.PIXELS_TO_METERS, 0);
    }
}
