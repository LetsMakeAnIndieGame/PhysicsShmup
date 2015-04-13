//package com.mygdx.game.actors;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.physics.box2d.World;
//import com.mygdx.game.PhysicsShmup;
//import com.mygdx.managers.*;
//
///**
// * Created by Phil on 1/12/2015.
// */
//public class Enemy extends PhysicsActor implements Lootable {
//    private int maxHealth = 200;
//    private int health = maxHealth;
//    private HealthBar healthBar;
//    private PhysicsShmup game;
//    private World world;
//
//    public Enemy(Texture texture, float x, float y, World world, PhysicsShmup game) {
//        super(texture, world);
//        setTexture(texture);
//        setColor(1, 0, 0, 1);
//        setPosition(x, y);
//        this.game = game;
//        this.world = world;
//
//        body = getHumanoidBody(PhysicsManager.ENEMY_BITS);
//
//        healthBar = new HealthBar(this, new Texture("Entities/Actors/enemyhealthbg.png"),
//                new Texture("Entities/Actors/enemyhealthfg.png"));
//    }
//
//    @Override
//    public void update() {
//        setPosition(body.getPosition().x * PhysicsManager.METERS_TO_PIXELS - getWidth() / 2,
//                body.getPosition().y * PhysicsManager.METERS_TO_PIXELS - 15f);
//
//        healthBar.update();
//    }
//
//    @Override
//    public void render(Batch batch) {
//        super.render(batch);
//        healthBar.render(batch);
//    }
//
//    @Override
//    public void handleSensorCollision(short categoryBits, boolean beginCollision) {
//
//    }
//
//    @Override
//    public void handleCollision(Object object) {
//        if (object instanceof Bullet) {
//            health -= 10;
//            new TextManager.DamageText("" + 10, getX() + getWidth() / 2, getY() + getHeight() / 2);
//            if (health <= 0) {
//                EntityManager.setToDestroy(this);
//            }
//        }
//    }
//
//    @Override
//    public void dropLoot() {
//        LootManager.generateEnemyLoot(this, world);
//    }
//
//    private class HealthBar {
//        private Sprite healthBarBG;
//        private Sprite healthBarFG;
//        private Enemy owner;
//        private final short buffer = 20;
//
//        public HealthBar(Enemy owner, Texture healthBG, Texture healthFG) {
//            this.owner = owner;
//
//            healthBarBG = new Sprite(healthBG);
//            healthBarFG = new Sprite(healthFG);
//
//            healthBarBG.setX(owner.getX());
//            healthBarBG.setY(owner.getY() + owner.getHeight() + buffer);
//            healthBarFG.setX(owner.getX());
//            healthBarFG.setY(owner.getY() + owner.getHeight() + buffer);
//            healthBarFG.setOrigin(0, 0);
//        }
//
//        public void update() {
//            healthBarBG.setX(owner.getX());
//            healthBarBG.setY(owner.getY() + owner.getHeight() + buffer);
//            healthBarFG.setX(owner.getX());
//            healthBarFG.setY(owner.getY() + owner.getHeight() + buffer);
//
//            healthBarFG.setScale(owner.health / (float) owner.maxHealth, 1f);
//        }
//
//        public void render(Batch batch) {
//            healthBarBG.draw(batch);
//            healthBarFG.draw(batch);
//        }
//    }
//}
