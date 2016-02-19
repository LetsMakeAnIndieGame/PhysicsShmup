package com.mygdx.managers;


import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.components.collision.TypeComponent;


/**
 * Based on code by:
 * @author David Saltares Márquez david.saltares at gmail.com
 * @brief Populates box2D world with static bodies using data from a map object
 */

public class LevelCollisionGenerator {
    private World world;
    private Array<Body> bodies = new Array<Body>();

    public LevelCollisionGenerator(World world) {
        this.world = world;
    }

    public void createPhysics(TiledMap map) {
        createPhysics(map, "CollisionLayer");
    }

    public void createPhysics(TiledMap map, String layerName) {
        MapLayer layer = map.getLayers().get(layerName);

        MapObjects objects = layer.getObjects();
        Iterator<MapObject> objectIt = objects.iterator();

        while(objectIt.hasNext()) {
            LevelGeometry geometry = null;
            MapObject object = objectIt.next();

            if (object instanceof TextureMapObject){
                continue;
            }

            Shape shape;
            BodyDef bodyDef = new BodyDef();
            bodyDef.awake = false;
            bodyDef.type = BodyDef.BodyType.StaticBody;

            if (object instanceof RectangleMapObject) {
                geometry = getRectangle((RectangleMapObject)object);
                shape = geometry.getShape();
            }
            else if (object instanceof PolygonMapObject) {
                geometry = getPolygon((PolygonMapObject)object);
                shape = geometry.getShape();
            }
            else if (object instanceof PolylineMapObject) {
                geometry = getPolyline((PolylineMapObject)object);
                shape = geometry.getShape();
            }
            else if (object instanceof CircleMapObject) {
                geometry = getCircle((CircleMapObject)object);
                shape = geometry.getShape();
            }
            else {
                Gdx.app.log("Unrecognized shape", "" + object.toString());
                continue;
            }

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = PhysicsManager.LEVEL_BITS;
            fixtureDef.filter.maskBits = (short) (PhysicsManager.FRIENDLY_BITS |
                    PhysicsManager.ENEMY_BITS |
                    PhysicsManager.NEUTRAL_BITS |
                    PhysicsManager.FOOT_SENSOR |
                    PhysicsManager.RIGHT_WALL_SENSOR |
                    PhysicsManager.LEFT_WALL_SENSOR);

            Body body = world.createBody(bodyDef);

            // All collisions need an entity, and all entities need a type to handle collisions
            Entity levelEntity = new Entity();
            TypeComponent type = new TypeComponent(PhysicsManager.COL_LEVEL);
            levelEntity.add(type);
            body.createFixture(fixtureDef).setUserData(levelEntity);

            bodies.add(body);

            fixtureDef.shape = null;
            shape.dispose();
        }
    }

    public void destroyPhysics() {
        for (Body body : bodies) {
            world.destroyBody(body);
        }

        bodies.clear();
    }

    private LevelGeometry getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) * PhysicsManager.PIXELS_TO_METERS,
                (rectangle.y + rectangle.height * 0.5f ) * PhysicsManager.PIXELS_TO_METERS);
        polygon.setAsBox(rectangle.width * 0.5f * PhysicsManager.PIXELS_TO_METERS,
                rectangle.height * 0.5f * PhysicsManager.PIXELS_TO_METERS,
                size,
                0.0f);
        return new LevelGeometry(polygon);
    }

    private LevelGeometry getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius * PhysicsManager.PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2(circle.x * PhysicsManager.PIXELS_TO_METERS, circle.y * PhysicsManager.PIXELS_TO_METERS));
        return new LevelGeometry(circleShape);
    }

    private LevelGeometry getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] * PhysicsManager.PIXELS_TO_METERS;
        }

        polygon.set(worldVertices);
        return new LevelGeometry(polygon);
    }

    private LevelGeometry getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] * PhysicsManager.PIXELS_TO_METERS;
            worldVertices[i].y = vertices[i * 2 + 1] * PhysicsManager.PIXELS_TO_METERS;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return new LevelGeometry(chain);
    }

    public static class LevelGeometry /*implements Collidable*/ {
        private Shape shape;

        public LevelGeometry(Shape shape) {
            this.shape = shape;
        }

        public Shape getShape() {
            return shape;
        }

//        @Override
//        public void handleSensorCollision(short categoryBits, boolean beginCollision) {
//
//        }
//
//        @Override
//        public void handleCollision(Object object) {
//        }
    }


}
