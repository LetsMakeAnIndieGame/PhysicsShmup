package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.components.physics.FauxGravityComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.physics.VelocityComponent;

/**
 * Created by Phil on 2/21/2015.
 */
public class FauxGravitySystem extends IteratingSystem {
    private ComponentMapper<FauxGravityComponent>   fauxGravityMap = ComponentMapper.getFor(FauxGravityComponent.class);
    private ComponentMapper<PositionComponent>      positionMap = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent>      velocityMap = ComponentMapper.getFor(VelocityComponent.class);

    public FauxGravitySystem() {
        super(Family.all(PositionComponent.class, FauxGravityComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionCom   = positionMap.get(entity);
        VelocityComponent velocityCom   = velocityMap.get(entity);
        float             gravity       = fauxGravityMap.get(entity).gravity;

        velocityCom.y -= gravity;

        positionCom.x += velocityCom.x;
        positionCom.y += velocityCom.y;
    }
}
