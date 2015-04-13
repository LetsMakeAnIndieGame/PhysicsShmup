package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.managers.PhysicsManager;

/**
 * Created by Phil on 2/9/2015.
 */
public class PositionSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent>  positionMap   = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent>    spriteMap     = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<BodyComponent>      bodyMap       = ComponentMapper.getFor(BodyComponent.class);

    public PositionSystem() {
        super(Family.all(PositionComponent.class).get());
    }

    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionCom  = positionMap.get(entity);
        SpriteComponent   spriteCom    = spriteMap.get(entity);
        BodyComponent     bodyCom      = bodyMap.get(entity);

        // Position priority: Body => PositionComponent => Sprites  (highest to lowest)
        if (bodyCom != null) {
            positionCom.x = bodyCom.body.getPosition().x * PhysicsManager.METERS_TO_PIXELS - spriteCom.sprites.get(0).getWidth() / 2;
            positionCom.y = bodyCom.body.getPosition().y * PhysicsManager.METERS_TO_PIXELS - spriteCom.sprites.get(0).getHeight() / 2;
        }

        if (spriteCom != null) {
            for (Sprite sprite : spriteCom.sprites) {
                sprite.setX(positionCom.x);
                sprite.setY(positionCom.y);
            }
        }
    }
}
