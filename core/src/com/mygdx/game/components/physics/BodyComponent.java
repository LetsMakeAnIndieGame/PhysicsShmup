package com.mygdx.game.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.managers.PhysicsManager;
import com.badlogic.gdx.utils.Pool.Poolable;


public class BodyComponent implements Component, Poolable {
    public Body body;

    public BodyComponent() {}

    public BodyComponent(PositionComponent positionCom, Body body) {
        setBodyAndPosition(positionCom, body);
    }

    public void setBodyAndPosition(PositionComponent positionCom, Body body) {
        this.body = body;
        this.body.setTransform(positionCom.x * PhysicsManager.PIXELS_TO_METERS, positionCom.y * PhysicsManager.PIXELS_TO_METERS, 0);
    }

    @Override
    public void reset() {
        body = null;
    }
}
