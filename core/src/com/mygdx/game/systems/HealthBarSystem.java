package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.managers.EntityManager;


public class HealthBarSystem extends IteratingSystem {
    ComponentMapper<AttachedComponent>  attachedMap = ComponentMapper.getFor(AttachedComponent.class);
    ComponentMapper<PositionComponent>  positionMap = ComponentMapper.getFor(PositionComponent.class);
    ComponentMapper<SpriteComponent>    spriteMap   = ComponentMapper.getFor(SpriteComponent.class);
    ComponentMapper<EnemyDataComponent> dataMap     = ComponentMapper.getFor(EnemyDataComponent.class);

    public HealthBarSystem() {
        super(Family.all(HealthBarComponent.class, AttachedComponent.class, PositionComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        PositionComponent posCom = positionMap.get(entity);
        AttachedComponent attCom = attachedMap.get(entity);
        SpriteComponent   spriteCom = spriteMap.get(entity);

        PositionComponent attPosCom = positionMap.get(attCom.attachedTo);
        SpriteComponent attSprite = spriteMap.get(attCom.attachedTo);
        EnemyDataComponent dataCom = dataMap.get(attCom.attachedTo);

        spriteCom.sprites.get(1).setScale(dataCom.health / (float) dataCom.maxHealth, 1);

        posCom.x = attPosCom.x;
        posCom.y = attPosCom.y + attSprite.sprites.first().getHeight() + 20; // 20 looks better with properly sized sprites

        if (spriteCom.sprites.get(1).getScaleX() <= 0) {
            entity.remove(RenderableComponent.class);
            EntityManager.setToDestroy(entity);
        }
    }
}
