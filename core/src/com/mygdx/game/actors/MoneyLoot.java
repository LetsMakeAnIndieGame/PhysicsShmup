//package com.mygdx.game.actors;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.World;
//import com.mygdx.managers.EntityManager;
//import com.mygdx.managers.PhysicsManager;
//
///**
// * Created by Phil on 1/22/2015.
// */
//public class MoneyLoot extends PhysicsActor {
//    private Texture texture;
//    private Body body;
//    private int amount;
//
//    public MoneyLoot(int amount, Texture texture, World world, float x, float y) {
//        super(texture, world);
//        setTexture(texture);
//        setPosition(x, y);
//        this.amount = amount;
//
//        body = getMoneyBody(PhysicsManager.NEUTRAL_BITS);
//        body.setAngularVelocity((float) (Math.random() - 0.5d) * 7f);
//        body.applyForceToCenter((float) (Math.random() - 0.5d) * 0.002f, 0.004f, true);
//        body.setUserData(this);
//    }
//
//    @Override
//    public void update() {
//        setPosition(body.getPosition().x  * PhysicsManager.METERS_TO_PIXELS - getWidth() / 2,
//                body.getPosition().y * PhysicsManager.METERS_TO_PIXELS - getHeight() / 2);
//        setRotation((float) Math.toDegrees(body.getAngle()));
//    }
//
//    @Override
//    public void render(Batch batch) {
//        draw(batch);
//    }
//
//    @Override
//    public void handleSensorCollision(short categoryBits, boolean beginCollision) {
//
//    }
//
//    @Override
//    public void handleCollision(Object object) {
//        if (object instanceof PhysicsPlayer) {
//            EntityManager.setToDestroy(this);
//        }
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//}
