package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.components.PlayerDataComponent;
import com.mygdx.game.components.graphics.SpriteComponent;

/**
 * Created by Phil on 2/15/2015.
 */
public class TurnaroundSystem extends IteratingSystem {
    private ComponentMapper<SpriteComponent> spriteMap = ComponentMapper.getFor(SpriteComponent.class);

    public TurnaroundSystem() {
        super(Family.all(SpriteComponent.class, PlayerDataComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        Sprite sprite = spriteMap.get(entity).sprites.first();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
            sprite.setFlip(!sprite.isFlipX(), false);
    }
}
