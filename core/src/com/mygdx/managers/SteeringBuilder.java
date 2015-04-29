package com.mygdx.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.actors.AirbornSteering;
import com.mygdx.game.actors.GroundSteering;
import com.mygdx.game.actors.Steering;

/**
 * Created by Phil on 4/13/2015.
 */
public class SteeringBuilder {
    private static final String path = "Entities/Steerings/";

    private static Steering steering = null;

    public static Steering createSteering(String filename, Entity entity) {
        JsonValue root = getRoot(filename);

        if (root.get("type").asString().equalsIgnoreCase("airborn")) {
            steering = new AirbornSteering(entity);
        } else if (root.get("type").asString().equalsIgnoreCase("ground")) {
            steering = new GroundSteering(entity);
        }

        steering.setMaxAngularAcceleration(root.get("maxAngularAcceleration").asFloat());
        steering.setMaxAngularSpeed(root.get("maxAngularSpeed").asFloat());
        steering.setMaxLinearAcceleration(root.get("maxLinearAcceleration").asFloat());
        steering.setMaxLinearSpeed(root.get("maxLinearSpeed").asFloat());

        return steering;
    }

    public static Steering createSteering(String filename, Vector2 position) {
        JsonValue root = getRoot(filename);

        if (root.get("type").asString().equalsIgnoreCase("airborn")) {
            steering = new AirbornSteering(position);
        } else if (root.get("type").asString().equalsIgnoreCase("ground")) {
            steering = new GroundSteering(position);
        }

        steering.setMaxAngularAcceleration(root.get("maxAngularAcceleration").asFloat());
        steering.setMaxAngularSpeed(root.get("maxAngularSpeed").asFloat());
        steering.setMaxLinearAcceleration(root.get("maxLinearAcceleration").asFloat());
        steering.setMaxLinearSpeed(root.get("maxLinearSpeed").asFloat());

        return steering;
    }

    private static JsonValue getRoot(String filename) {
        FileHandle handle = Gdx.files.internal(path + filename);

        String      rawJson     = handle.readString();
        JsonReader  jsonReader  = new JsonReader();
        JsonValue   root        = jsonReader.parse(rawJson);

        return root;
    }
}
