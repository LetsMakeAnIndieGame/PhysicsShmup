package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.PhysicsShmup;
import com.mygdx.game.actors.Collidable;
import com.mygdx.game.actors.FlyingTestEnemyComponent;
import com.mygdx.game.components.*;
import com.mygdx.game.components.graphics.BitmapFontComponent;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.graphics.TransparentComponent;
import com.mygdx.game.components.physics.FauxGravityComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.physics.VelocityComponent;
import com.mygdx.managers.EntityManager;
import com.mygdx.managers.PhysicsManager;
import com.mygdx.managers.RenderPriority;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EnemyCollisionComponent implements Component, Collidable, Poolable {
    private ComponentMapper<TypeComponent> cm = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<BulletDamageComponent> bm = ComponentMapper.getFor(BulletDamageComponent.class);
    private ComponentMapper<EnemyDataComponent> em = ComponentMapper.getFor(EnemyDataComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<FlyingTestEnemyComponent> am = ComponentMapper.getFor(FlyingTestEnemyComponent.class);

    private GlyphLayout glyphLayout;

    public EnemyCollisionComponent() {}

    @Override
    public void handleCollision(PooledEngine engine, Entity collider, Entity collidee) {
        short type = cm.get(collidee).type;

        EnemyDataComponent data = em.get(collider);

        if (type == PhysicsManager.COL_FRIENDLY_BULLET) {
            am.get(collider).isShot = true;
            long damage = bm.get(collidee).damage;
            Sprite sprite = sm.get(collider).sprites.get(0);

            Entity indicator = engine.createEntity();

            BitmapFontComponent bFontCom = engine.createComponent(BitmapFontComponent.class);
            bFontCom.bFont = new BitmapFont(Gdx.files.internal("Entities/Scene2D/damage.fnt"));
            bFontCom.msg = "" + damage;

            // Because bitmap font getBounds is deprecated, need to use glyphLayout :(
            glyphLayout = new GlyphLayout(bFontCom.bFont, bFontCom.msg);
//            glyphLayout.setText(bFontCom.bFont, bFontCom.msg);
            float textWidth = glyphLayout.width;
            float textHeight = glyphLayout.height;

            PositionComponent posCom = engine.createComponent(PositionComponent.class);
            posCom.x = sprite.getX() + sprite.getWidth() / 2 - textWidth / 2;
            posCom.y = sprite.getY() + sprite.getHeight() / 2 + textHeight / 2;
            posCom.z = RenderPriority.HIGH;

            VelocityComponent vCom = engine.createComponent(VelocityComponent.class);
            vCom.x = (float) (Math.random() - 0.5d) * 2; // make these random
            vCom.y = 3;

            RenderableComponent renderCom = engine.createComponent(RenderableComponent.class);

            TransparentComponent transCom = engine.createComponent(TransparentComponent.class);
            transCom.transparency = 1;

            DeathTimerComponent deathCom  = engine.createComponent(DeathTimerComponent.class);
            deathCom.deathTime = 2000; // die after 2 seconds?
            deathCom.createTime = PhysicsShmup.currentTimeMillis;

            FauxGravityComponent fauxGCom = engine.createComponent(FauxGravityComponent.class);
            fauxGCom.gravity = 0.07f;

            indicator.add(posCom).add(vCom).add(bFontCom).add(renderCom).add(deathCom).add(transCom).add(fauxGCom);

            engine.addEntity(indicator);

            EntityManager.add(indicator);

            if ((data.health -= damage) <= 0) {
                collider.remove(RenderableComponent.class);
                EntityManager.setToDestroy(collider);
            }
        }
    }

    @Override
    public void reset() {
        // do nothing
    }
}
