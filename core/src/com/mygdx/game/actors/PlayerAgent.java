package com.mygdx.game.actors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.VelocityComponent;
import com.mygdx.game.components.states.SensorCollisionComponent;
import com.mygdx.managers.GameInput;

/**
 * Created by Phil on 2/24/2015.
 */
public class PlayerAgent implements Updateable {
    public StateMachine<PlayerAgent> stateMachine;
    public StateMachine<PlayerAgent> subStateMachine;
    public boolean isTouchingGround = true;
    public boolean isTouchingWallLeft = false;
    public boolean isTouchingWallRight = false;
    public boolean hasWallSlid = false;
    public long lastWallJump = 0;

    private Entity entity;
    private ComponentMapper<BodyComponent>              bodyMap         = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<VelocityComponent>          velocityMap     = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<SensorCollisionComponent>   sensorColMap    = ComponentMapper.getFor(SensorCollisionComponent.class);

    public PlayerAgent(Entity entity) {
        this.entity = entity;
        stateMachine = new DefaultStateMachine<PlayerAgent>(this, PlayerState.GROUNDED);
        subStateMachine = new DefaultStateMachine<PlayerAgent>(this, PlayerSubState.NONE);
    }

    public float getBodyVelocityY() {
        return bodyMap.get(entity).body.getLinearVelocity().y;
    }

    public void setBodyVelocity(float x, float y) {
        bodyMap.get(entity).body.setLinearVelocity(x, y);
    }

    public void update(float delta) {
        // Update the boolean/other state helper data here, and the state machines
        isTouchingGround = (sensorColMap.get(entity).numFoot > 0);
        isTouchingWallLeft = (sensorColMap.get(entity).numLeftWall > 0);
        isTouchingWallRight = (sensorColMap.get(entity).numRightWall > 0);
        stateMachine.update();
        subStateMachine.update();
    }

    public void moveOnGround() {
        float   velocityX = velocityMap.get(entity).x;
        Body    body      = bodyMap.get(entity).body;
        float   momentumX = body.getLinearVelocity().x;

        float acceleration = 0.3f;
        float desiredVelocity = Math.max(body.getLinearVelocity().x - acceleration,
                Math.min(GameInput.KeyForce.x * velocityX, body.getLinearVelocity().x + acceleration));

        float velocityChange = desiredVelocity - momentumX;
        float impulse = body.getMass() * velocityChange;
        body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            body.applyForceToCenter(0, 70f, true);
    }

    public void moveInAir() {
        Body  body          = bodyMap.get(entity).body;
        float momentumX     = body.getLinearVelocity().x;
        float velocityX     = velocityMap.get(entity).x;

        float acceleration  = 0.05f;
        float desiredVelocity = Math.max(body.getLinearVelocity().x - acceleration,
                Math.min(GameInput.KeyForce.x * velocityX, body.getLinearVelocity().x + acceleration));

        float velocityChange = desiredVelocity - momentumX;
        float impulse = body.getMass() * velocityChange;
        body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
    }

    public void wallJump(float jumpForce, float pushForce) {
        Body body = bodyMap.get(entity).body;
        SensorCollisionComponent sensorColCom = sensorColMap.get(entity);

        // You need to increase the jumpForce based on how fast you're falling (if you're falling) so that it's consistent
        // p = mv (p being intertia)
        float modifiedJumpForce = jumpForce + jumpForce * body.getMass() * Math.abs(body.getLinearVelocity().y);

        body.applyForceToCenter(pushForce * (Math.signum(sensorColCom.numLeftWall) + Math.signum(-sensorColCom.numRightWall)), modifiedJumpForce, true);
    }
}
