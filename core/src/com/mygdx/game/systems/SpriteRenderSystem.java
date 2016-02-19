package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.PositionComponent;

import javax.swing.text.Position;
import java.util.Comparator;


public class SpriteRenderSystem extends SortedIteratingSystem {
    private SpriteBatch batch;

    private ComponentMapper<SpriteComponent> spriteMap = ComponentMapper.getFor(SpriteComponent.class);

    public SpriteRenderSystem(SpriteBatch batch) {
        super(Family.all(RenderableComponent.class, SpriteComponent.class, PositionComponent.class).get(), new ZComparator());
        this.batch = batch;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        for (Sprite sprite : spriteMap.get(entity).sprites) {
            sprite.draw(batch);
        }

//        batch.draw(sprite.getTexture(), sprite.getX(), sprite.getY(), sprite.getWidth() / 2, sprite.getHeight() / 2, sprite.getWidth(), sprite.getHeight(), 1, 1, sprite.getRotation(), 0, 0, (int) sprite.getWidth(), (int) sprite.getHeight(), isLeft, false);
    }
}
