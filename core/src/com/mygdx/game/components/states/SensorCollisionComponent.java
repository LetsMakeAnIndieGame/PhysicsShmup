package com.mygdx.game.components.states;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class SensorCollisionComponent implements Component, Poolable {
    public short numFoot        = 0;
    public short numRightWall   = 0;
    public short numLeftWall    = 0;

    public SensorCollisionComponent() {}

    @Override
    public void reset() {
        numFoot = 0;
        numRightWall = 0;
        numLeftWall = 0;
    }
}
