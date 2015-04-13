package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.PhysicsShmup;
import com.mygdx.game.components.graphics.BitmapFontComponent;
import com.mygdx.game.components.DeathTimerComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.graphics.TransparentComponent;
import com.mygdx.managers.EntityManager;

/**
 * Created by Phil on 2/21/2015.
 */
public class DeathTimerSystem extends IteratingSystem {
    ComponentMapper<DeathTimerComponent>    deathTimerMap   = ComponentMapper.getFor(DeathTimerComponent.class);
    ComponentMapper<TransparentComponent>   transparencyMap = ComponentMapper.getFor(TransparentComponent.class);
    ComponentMapper<BitmapFontComponent>    bitmapFontMap   = ComponentMapper.getFor(BitmapFontComponent.class);
    ComponentMapper<SpriteComponent>        spriteMap       = ComponentMapper.getFor(SpriteComponent.class);

    public DeathTimerSystem() {
        super(Family.all(DeathTimerComponent.class, TransparentComponent.class)
                .one(SpriteComponent.class, BitmapFontComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        DeathTimerComponent     deathCom = deathTimerMap.get(entity);
        TransparentComponent    transCom = transparencyMap.get(entity);
        BitmapFontComponent     bFontCom = bitmapFontMap.get(entity);
        SpriteComponent         spriteCom = spriteMap.get(entity);

        long deathDelta = PhysicsShmup.currentTimeMillis - deathCom.createTime;

        transCom.transparency = 1 - (float) Math.pow(deathDelta / (double) deathCom.deathTime, 2.0d);

        if (bFontCom != null) {
            bFontCom.bFont.setColor(1, 1, 1, transCom.transparency);
        }
        if (spriteCom != null) {
            spriteCom.sprites.get(0).setAlpha(transCom.transparency);
        }

        if (deathCom.deathTime + deathCom.createTime <= PhysicsShmup.currentTimeMillis) {
            EntityManager.setToDestroy(entity);
        }
    }
}
