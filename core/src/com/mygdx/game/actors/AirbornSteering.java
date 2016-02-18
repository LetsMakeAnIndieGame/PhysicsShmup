package com.mygdx.game.actors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.pathfinding.PathfindingDebugger;
import com.mygdx.managers.PhysicsManager;

import java.math.BigDecimal;

/**
 * Created by Phil on 4/13/2015.
 */
public class AirbornSteering extends Steering implements Steerable<Vector2>, Updateable {
    public AirbornSteering(Vector2 position) {
        super(position);
    }

    public AirbornSteering(Entity entity) {
        super(entity);
    }

    @Override
    public void update (float delta) {
        if (steeringBehavior != null) {
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringOutput);

            // Apply steering acceleration to move this agent
            applySteering(steeringOutput, 1f);
        }
    }

    @Override
    protected void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        Body body = bodyCom.body;
        linearVelocity = body.getLinearVelocity();
        Vector2 acceleration = steering.linear;

        // add the acceleration to the velocity but clamp it to maxLinearSpeed (using vector functions to add and such is probably better)
        float desiredVelocityX = Math.min(maxLinearSpeed, Math.max(-maxLinearSpeed, linearVelocity.x + acceleration.x));
        float desiredVelocityY = Math.min(maxLinearSpeed, Math.max(-maxLinearSpeed, linearVelocity.y + acceleration.y));

        // This isn't right, but it works....change this when you figure out how to math
        if (acceleration.x == 0) {
            desiredVelocityX = 0;
        }
        if (acceleration.y == 0) {
            desiredVelocityY = 0;
        }

        // Gross
        if (Math.abs(linearVelocity.x) < minLinearSpeed) {
            linearVelocity.x = 0;
        }
        if (Math.abs(linearVelocity.y) < minLinearSpeed) {
            linearVelocity.y = 0;
        }

        float velocityDeltaX = desiredVelocityX - linearVelocity.x; // adjust these for diagonal movement (sin, cos)
        float velocityDeltaY = desiredVelocityY - linearVelocity.y;

        Vector2 impulse = new Vector2(body.getMass() * velocityDeltaX, body.getMass() * velocityDeltaY);

        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
        position.x = body.getPosition().x * PhysicsManager.METERS_TO_PIXELS;
        position.y = body.getPosition().y * PhysicsManager.METERS_TO_PIXELS;

        PathfindingDebugger.drawPoint2Point(position, target.getPosition());

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

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }
}
