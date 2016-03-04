package com.mygdx.game.components.collision;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.actors.Collidable;
import com.mygdx.game.actors.FlyingTestEnemyComponent;
import com.mygdx.game.components.fsm.EnemyAgentComponent;
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

public class EnemyCollisionComponent implements Component, Collidable {
    private ComponentMapper<TypeComponent> cm = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<BulletDamageComponent> bm = ComponentMapper.getFor(BulletDamageComponent.class);
    private ComponentMapper<EnemyDataComponent> em = ComponentMapper.getFor(EnemyDataComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<FlyingTestEnemyComponent> am = ComponentMapper.getFor(FlyingTestEnemyComponent.class);

    private GlyphLayout glyphLayout;

    @Override
    public void handleCollision(Engine engine, Entity collider, Entity collidee) {
        short type = cm.get(collidee).type;

        EnemyDataComponent data = em.get(collider);

        if (type == PhysicsManager.COL_FRIENDLY_BULLET) {
            am.get(collider).isShot = true;
            long damage = bm.get(collidee).damage;
            Sprite sprite = sm.get(collider).sprites.get(0);

            Entity indicator = new Entity();
            BitmapFontComponent bFontCom = new BitmapFontComponent(Gdx.files.internal("Entities/Scene2D/damage.fnt"), "" + damage);
            // Because bitmap font getBounds is deprecated, need to use glyphLayout :(
            glyphLayout = new GlyphLayout(bFontCom.bFont, bFontCom.msg);
//            glyphLayout.setText(bFontCom.bFont, bFontCom.msg);
            float textWidth = glyphLayout.width;
            float textHeight = glyphLayout.height;
            PositionComponent posCom = new PositionComponent(sprite.getX() + sprite.getWidth() / 2 - textWidth / 2, sprite.getY() + sprite.getHeight() / 2 + textHeight / 2, RenderPriority.HIGH);
            VelocityComponent vCom = new VelocityComponent((float) (Math.random() - 0.5d) * 2, 3); // make these random
            RenderableComponent renderCom = new RenderableComponent();
            TransparentComponent transCom = new TransparentComponent(1);
            DeathTimerComponent deathCom  = new DeathTimerComponent(2000); // die after 2 seconds?
            FauxGravityComponent fauxGCom = new FauxGravityComponent(0.07f);

            indicator.add(posCom).add(vCom).add(bFontCom).add(renderCom).add(deathCom).add(transCom).add(fauxGCom);

            engine.addEntity(indicator);

            EntityManager.add(indicator);

            if ((data.health -= damage) <= 0) {
                collider.remove(RenderableComponent.class);
                EntityManager.setToDestroy(collider);
            }
        }
    }
}
