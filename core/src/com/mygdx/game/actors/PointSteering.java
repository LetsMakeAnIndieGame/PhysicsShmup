package com.mygdx.game.actors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.physics.PositionComponent;

/**
 * Created by Phil on 4/24/2015.
 */
public class PointSteering extends Steering implements Steerable<Vector2>, Updateable {
    protected ComponentMapper<PositionComponent> positionMap  = ComponentMapper.getFor(PositionComponent.class);

    public PointSteering(Vector2 position) { super(position); }

    public PointSteering(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        PositionComponent posCom = positionMap.get(entity);

        position.x = posCom.x;
        position.y = posCom.y;
    }
}
