package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.components.graphics.BitmapFontComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.graphics.RenderableComponent;

/**
 * Created by Phil on 2/21/2015.
 */
public class BitmapFontRenderSystem extends IteratingSystem {
    private SpriteBatch batch;

    private ComponentMapper<BitmapFontComponent> bm = ComponentMapper.getFor(BitmapFontComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public BitmapFontRenderSystem(SpriteBatch batch) {
        super(Family.all(RenderableComponent.class, BitmapFontComponent.class, PositionComponent.class).get());
        this.batch = batch;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        BitmapFont bFont = bm.get(entity).bFont;
        CharSequence msg = bm.get(entity).msg;
        float x = pm.get(entity).x;
        float y = pm.get(entity).y;

        bFont.draw(batch, msg, x, y);
    }
}
