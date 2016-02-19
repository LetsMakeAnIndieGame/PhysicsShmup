package com.mygdx.game.actors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.physics.BodyComponent;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.components.physics.VelocityComponent;
import com.mygdx.game.pathfinding.GraphPathImp;

public abstract class Steering implements Steerable<Vector2>, Updateable {
    protected ComponentMapper<PositionComponent> positionMap  = ComponentMapper.getFor(PositionComponent.class);
    protected ComponentMapper<BodyComponent>     bodyMap      = ComponentMapper.getFor(BodyComponent.class);
    protected ComponentMapper<VelocityComponent> velocityMap  = ComponentMapper.getFor(VelocityComponent.class);

    protected static final SteeringAcceleration<Vector2> steeringOutput =
            new SteeringAcceleration<Vector2>(new Vector2());

    protected Vector2 position;
    protected Vector2 linearVelocity;
    protected float orientation;
    protected float angularVelocity;

    protected float maxLinearSpeed;
    protected float minLinearSpeed;
    protected float maxLinearAcceleration;
    protected float maxAngularSpeed;
    protected float maxAngularAcceleration;

    protected boolean independentFacing;
    protected BodyComponent bodyCom;
    protected Entity entity;
    protected Steerable<Vector2> target;

    protected SteeringBehavior<Vector2> steeringBehavior;

    /*
    Constructors
     */

    public Steering() {}

    public Steering(Vector2 position) {
        this.position = position;
    }

    public Steering(Entity entity) {
        independentFacing = true;

        this.entity = entity;

        position        = new Vector2(positionMap.get(entity).x, positionMap.get(entity).y);
        linearVelocity  = new Vector2(velocityMap.get(entity).x, velocityMap.get(entity).y);
        bodyCom         = bodyMap.get(entity);
    }

    /*
    Helpers
     */

    @Override
    public float vectorToAngle (Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector (Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    public static float calculateOrientationFromLinearVelocity (Steerable<Vector2> character) {
        // If we haven't got any velocity, then we can do nothing.
        if (character.getLinearVelocity().isZero(MathUtils.FLOAT_ROUNDING_ERROR))
            return character.getOrientation();

        return character.vectorToAngle(character.getLinearVelocity());
    }

    // This will probably suffice for most if not all cases
    public void update (float delta) {
        if (steeringBehavior != null) {
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringOutput);

            // Apply steering acceleration to move this agent
            applySteering(steeringOutput, 1f);
        }
    }

    protected void applySteering (SteeringAcceleration<Vector2> steering, float time) {
        // must be implemented by child
    }

    /*
    Setters and Getters
     */

    public void setSteeringBehavior(Class<?> behaviorType) {
        if (behaviorType == Arrive.class) {
            steeringBehavior = new Arrive<Vector2>(this, target).setDecelerationRadius(10).setArrivalTolerance(3);
        } else if (behaviorType == Flee.class) {
            steeringBehavior = new Flee<Vector2>(this, target);
        } else if (behaviorType == Seek.class) {
            steeringBehavior = new Seek<Vector2>(this, target);
        }
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setTarget(Steerable<Vector2> target) {
        this.target = target;
        if (steeringBehavior == null) {
            steeringBehavior = new Arrive<Vector2>(this, target).setDecelerationRadius(10).setArrivalTolerance(3);
        }
    }

    public Steerable<Vector2> getTarget() {
        return target;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    @Override
    public float getBoundingRadius() {
        return 0;
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {
        // ignoring this
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    public float getMinLinearSpeed() { return minLinearSpeed; }

    public void setMinLinearSpeed(float minLinearSpeed) { this.minLinearSpeed = minLinearSpeed; }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }
}
