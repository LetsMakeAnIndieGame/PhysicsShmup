package com.mygdx.managers;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;


public class Lighting {
    private static World world;
    private static TiledMap tiledMap;
    private static Array<Light> lights = new Array<Light>();
    private static final int MAX_LIGHTS = 8;
    private static final int MAX_RAYS = 1000;
    private static final int MIN_RAYS = 80;
    private static RayHandler rayHandler;

    public static void createLights(World w, TiledMap t) {
        world = w;
        tiledMap = t;

        int numLights = 0;
        float distance;

        rayHandler = new RayHandler(world);

        MapObjects objects = tiledMap.getLayers().get("Lighting").getObjects();
        Iterator<MapObject> objectIterator = objects.iterator();

        while(objectIterator.hasNext() && numLights < MAX_LIGHTS) {
            Light light;
            MapObject object = objectIterator.next();
            String result = "null";

            // PointLight, DirectionalLight, PositionalLight, ChainLight, ConeLight
            result = (String) object.getProperties().get("Type");

            if (result == "Point") {
                light = new PointLight(rayHandler, MAX_RAYS);
            } else {
                light = new PointLight(rayHandler, MAX_RAYS);
            }

            try {
                distance = Float.parseFloat((String) object.getProperties().get("Distance"));
            } catch (Exception e) {
                distance = 8;
            }

            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                light.setColor(Color.BLUE);
                light.setDistance(distance);
                light.setPosition((rectangle.x + rectangle.getWidth() / 2) * PhysicsManager.PIXELS_TO_METERS,
                        (rectangle.y + rectangle.getHeight() / 2) * PhysicsManager.PIXELS_TO_METERS);
                lights.add(light);
            }
            else if (object instanceof CircleMapObject) {
                Circle circle = ((CircleMapObject) object).getCircle();
                light.setColor(Color.BLUE);
                light.setDistance(distance);
                light.setPosition(circle.x * PhysicsManager.PIXELS_TO_METERS, circle.y * PhysicsManager.PIXELS_TO_METERS);
                lights.add(light);
            } else {
                continue;
            }

            numLights++;
        }
    }

    public static void updateAndShowLights(Camera camera) {
        if (lights.size > 0) {
            rayHandler.setCombinedMatrix(camera.combined.cpy().scale(PhysicsManager.METERS_TO_PIXELS,
                    PhysicsManager.METERS_TO_PIXELS, 1f));
            rayHandler.updateAndRender();
        }
    }

    public static void destroyLights() {
        for (Light light : lights) {
            light.dispose();
        }

        lights.clear();
    }
}
