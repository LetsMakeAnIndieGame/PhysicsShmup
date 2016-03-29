package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.managers.GameInput;
import com.mygdx.managers.SoundManager;

import javax.swing.text.Position;


public class CrosshairSystem extends IteratingSystem {
    private ComponentMapper<AttachedComponent>  attachedMap     = ComponentMapper.getFor(AttachedComponent.class);
    private ComponentMapper<SpriteComponent>    spriteMap       = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent>  positionMap     = ComponentMapper.getFor(PositionComponent.class);

    public CrosshairSystem() {
        super(Family.all(AttachedComponent.class,
                CrosshairComponent.class,
                PositionComponent.class,
                RenderableComponent.class,
                SpriteComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        Sprite            thisSprite        = spriteMap.get(entity).sprites.first();
        PositionComponent position          = positionMap.get(entity);

//        thisSprite.setX((float) ((attachedSprite.getX() + attachedSprite.getWidth() / 2) - (thisSprite.getWidth() / 2) + Math.sin(-Math.toRadians(angle)) * distance));
//        thisSprite.setY((float) ((attachedSprite.getY() + attachedSprite.getHeight() / 2) - (thisSprite.getHeight() / 2) + Math.cos(Math.toRadians(angle)) * distance));

        position.x += GameInput.MouseForce.x * 0.7f;
        position.y += GameInput.MouseForce.y * 0.7f;
    }
}
