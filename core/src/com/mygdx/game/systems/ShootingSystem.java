package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PhysicsShmup;
import com.mygdx.game.components.*;
import com.mygdx.game.components.collision.BulletCollisionComponent;
import com.mygdx.game.components.collision.TypeComponent;
import com.mygdx.game.components.graphics.RenderableComponent;
import com.mygdx.game.components.graphics.SpriteComponent;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.managers.BodyGenerator;
import com.mygdx.managers.EntityManager;
import com.mygdx.managers.PhysicsManager;

/**
 * Created by Phil on 2/14/2015.
 */
public class ShootingSystem extends IteratingSystem {
    private Engine engine;
    private World world;

    private ComponentMapper<LookAngleComponent> lookAngleMap    = ComponentMapper.getFor(LookAngleComponent.class);
    private ComponentMapper<ShootingComponent>  shootingMap     = ComponentMapper.getFor(ShootingComponent.class);
    private ComponentMapper<SpriteComponent>    spriteMap       = ComponentMapper.getFor(SpriteComponent.class);


    public ShootingSystem(Engine engine, World world) {
        super(Family.all(LookAngleComponent.class, ShootingComponent.class, SpriteComponent.class).get());
        this.engine = engine;
        this.world = world;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        float  angle          = lookAngleMap.get(entity).angle;
        long   lastBulletTime = shootingMap.get(entity).lastBulletTime;
        Sprite sprite         = spriteMap.get(entity).sprites.first();
        int bulletDelayMillis = 350;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (PhysicsShmup.currentTimeMillis - lastBulletTime > bulletDelayMillis) {
                Entity eBullet = new Entity();
                RenderableComponent renderComponent   = new RenderableComponent();
                PositionComponent positionComponent = new PositionComponent(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 2, 900);
                SpriteComponent     spriteComponent   = new SpriteComponent(new Texture("Entities/Actors/laser.png"));
                BulletDamageComponent damageComponent = new BulletDamageComponent(10);
                BulletCollisionComponent bulletColCom = new BulletCollisionComponent();
                TypeComponent typeComponent     = new TypeComponent(PhysicsManager.COL_FRIENDLY_BULLET);
                BodyComponent bodyComponent     = new BodyComponent(positionComponent,
                        BodyGenerator.generateBody(eBullet,
                                spriteComponent.sprites.first(),
                                Gdx.files.internal("Entities/BodyDefinitions/Laser.json"),
                                (short) (PhysicsManager.FRIENDLY_BITS | PhysicsManager.NEUTRAL_BITS)));

                eBullet.add(positionComponent).add(spriteComponent).add(bodyComponent).add(renderComponent)
                        .add(damageComponent).add(bulletColCom).add(typeComponent);

                Body bullet = bodyComponent.body;
                Sprite bSprite = spriteComponent.sprites.first();
                bullet.setTransform(bullet.getPosition().x, bullet.getPosition().y, (float) Math.toRadians(angle - 90f));
                bullet.applyLinearImpulse(new Vector2(0.004f * (float) Math.sin(Math.toRadians(-angle)),
                        0.004f * (float) Math.cos(Math.toRadians(angle))), bullet.getPosition(), true);
                bSprite.setRotation((float) Math.toDegrees(bullet.getAngle()));

                shootingMap.get(entity).lastBulletTime = PhysicsShmup.currentTimeMillis;

                engine.addEntity(eBullet);
                EntityManager.add(eBullet);
            }
        }
    }
}
