package com.mygdx.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Phil on 2/8/2015.
 */
public class BodyGenerator {
    private static World world;

    public static void registerWorld(World world) {
        BodyGenerator.world = world;
    }

    public static Body generateBody(Entity owner, Vector2 position, BitmapFont image, CharSequence msg, FileHandle handle, short filterCategory) {
        BitmapFont.TextBounds bounds = image.getBounds(msg);
        return bodyHelper(owner, position, new Vector2(bounds.width, bounds.height), handle, filterCategory);
    }

    public static Body generateBody(Entity owner, Sprite image, FileHandle handle, short filterCategory) {
        return bodyHelper(owner, new Vector2(image.getX(), image.getY()), new Vector2(image.getWidth(), image.getHeight()), handle, filterCategory);
    }

    public static Body bodyHelper(Entity owner, Vector2 position, Vector2 dimensions, FileHandle handle, short filterCategory) {
        Body body;

        String     rawJson        = handle.readString();
        JsonReader jsonReader = new JsonReader();
        JsonValue  root        = jsonReader.parse(rawJson);

        short maskingBits = (short) ((PhysicsManager.FRIENDLY_BITS | PhysicsManager.ENEMY_BITS | PhysicsManager.NEUTRAL_BITS | PhysicsManager.LEVEL_BITS) ^ filterCategory);

        BodyDef bodyDef = new BodyDef();

        String bodyType = root.get("BodyDef").getString("type");
        if (bodyType.equalsIgnoreCase("DynamicBody"))
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        else if (bodyType.equalsIgnoreCase("KinematicBody"))
            bodyDef.type = BodyDef.BodyType.KinematicBody;
        else if (bodyType.equalsIgnoreCase("StaticBody"))
            bodyDef.type = BodyDef.BodyType.StaticBody;
        else
            Gdx.app.log("WARNING", "Entity Box2D body type undefined - " + filterCategory);

        JsonValue jsonBody = root.get("BodyDef");

        bodyDef.bullet = jsonBody.getBoolean("bullet");
        bodyDef.fixedRotation = jsonBody.getBoolean("fixedRotation");
        bodyDef.gravityScale = jsonBody.getFloat("gravityScale");

        bodyDef.position.set((position.x + dimensions.x / 2) * PhysicsManager.PIXELS_TO_METERS,
                (position.y + dimensions.y / 2) * PhysicsManager.PIXELS_TO_METERS);

        body = world.createBody(bodyDef);

        JsonValue fixtures = root.get("Fixtures");
        for (JsonValue fixture : fixtures) {

            String fixtureType = fixture.getString("type");
            Shape shape;

            if (fixtureType.equalsIgnoreCase("PolygonShape")) {
                shape = new PolygonShape();
                ((PolygonShape) shape).setAsBox(fixture.getFloat("width") * PhysicsManager.PIXELS_TO_METERS,
                        fixture.getFloat("height") * PhysicsManager.PIXELS_TO_METERS,
                        new Vector2((body.getLocalCenter().x + fixture.getFloat("x")) * PhysicsManager.PIXELS_TO_METERS,
                                (body.getLocalCenter().y + fixture.getFloat("y")) * PhysicsManager.PIXELS_TO_METERS), 0f);

            } else if (fixtureType.equalsIgnoreCase("CircleShape")) {
                shape = new CircleShape();
                shape.setRadius(fixture.getFloat("radius") * PhysicsManager.PIXELS_TO_METERS);
                ((CircleShape) shape).setPosition(new Vector2((body.getLocalCenter().x + fixture.getFloat("x")) * PhysicsManager.PIXELS_TO_METERS,
                        (body.getLocalCenter().y + fixture.getFloat("y")) * PhysicsManager.PIXELS_TO_METERS));
            } else {
                Gdx.app.log("WARNING", "Generated body shape was invalid");
                continue;
            }

            boolean isSensor = fixture.getBoolean("isSensor");

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = isSensor;
            fixtureDef.density = fixture.getFloat("density");
            if (isSensor) {
                fixtureDef.filter.categoryBits = (short) (filterCategory << fixture.getShort("bitShifts"));
                fixtureDef.filter.maskBits = PhysicsManager.LEVEL_BITS;
            } else {
                fixtureDef.friction = fixture.getFloat("friction");
                fixtureDef.filter.categoryBits = filterCategory;
                fixtureDef.filter.maskBits = maskingBits;
            }
            body.createFixture(fixtureDef).setUserData(owner);
            shape.dispose();
        }

        return body;
    }
}
