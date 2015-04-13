package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.managers.GameInput;

/**
 * Created by Phil on 2/9/2015.
 */
public class AimingSystem extends IteratingSystem {
    private ComponentMapper<LookAngleComponent> lookAngleMap = ComponentMapper.getFor(LookAngleComponent.class);
    private ComponentMapper<SpriteComponent>    spriteMap    = ComponentMapper.getFor(SpriteComponent.class);

    public AimingSystem() {
        super(Family.all(LookAngleComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        LookAngleComponent lookAngleCom = lookAngleMap.get(entity);
        SpriteComponent    spriteCom    = spriteMap.get(entity);

        lookAngleCom.angle = Math.min(160, Math.max(20, Math.abs(lookAngleCom.angle) - GameInput.MouseForce.y / 2.5f));

        if (!spriteCom.sprites.first().isFlipX())
            lookAngleCom.angle *= -1;
    }
}
