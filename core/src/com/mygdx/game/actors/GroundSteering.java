package com.mygdx.game.actors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.managers.PhysicsManager;

/**
 * Created by Phil on 4/15/2015.
 */
public class GroundSteering extends Steering implements Steerable<Vector2>, Updateable {
    public GroundSteering(Vector2 position) {
        super(position);
    }

    public GroundSteering(Entity entity) {
        super(entity);
    }

    @Override
    protected void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        this.linearVelocity.mulAdd(steering.linear, time).limit(this.getMaxLinearSpeed());

        Body body = bodyCom.body;
        linearVelocity = body.getLinearVelocity();
        float   velocityX = linearVelocity.x;

        float desiredVelocity = Math.max(velocityX - maxLinearSpeed,
                Math.min(Math.signum(steering.linear.x) * maxLinearAcceleration, velocityX + maxLinearSpeed));

        desiredVelocity = Math.min(desiredVelocity, maxLinearSpeed);

        float velocityChange = desiredVelocity - velocityX;
        float impulse = body.getMass() * velocityChange;
        body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);

        position.x = body.getPosition().x * PhysicsManager.METERS_TO_PIXELS;
        position.y = body.getPosition().y * PhysicsManager.METERS_TO_PIXELS;

        // Update orientation and angular velocity
        if (independentFacing) {
            this.orientation += angularVelocity * time;
            this.angularVelocity += steering.angular * time;
        } else {
            // For non-independent facing we have to align orientation to linear velocity
            float newOrientation = calculateOrientationFromLinearVelocity(this);
            if (newOrientation != this.orientation) {
                this.angularVelocity = (newOrientation - this.orientation) * time;
                this.orientation = newOrientation;
            }
        }
    }
}
