package com.mygdx.game.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.managers.LevelManager;

import java.util.Iterator;


public class PathfindingDebugger {
    private static OrthographicCamera camera;
    private static ShapeRenderer shapeRenderer;

    public static void setCamera(OrthographicCamera camera) {
        PathfindingDebugger.camera = camera;
        shapeRenderer = new ShapeRenderer();
    }

    public static void drawPoint2Point(Vector2 pos1, Vector2 pos2) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 1, 1);
        shapeRenderer.circle(pos1.x, pos1.y, 5);
        shapeRenderer.circle(pos2.x, pos2.y, 5);
//        shapeRenderer.line(pos1, pos2, 10);
        shapeRenderer.rectLine(pos1, pos2, 3);
        shapeRenderer.end();
    }

    public static void drawPath(GraphPathImp path) {
        Iterator<Node> pathIterator = path.iterator();
        Node priorNode = null;

        while (pathIterator.hasNext()) {
            Node node = pathIterator.next();

            int index = node.getIndex();

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.circle(LevelManager.tilePixelWidth / 2 + (index % LevelManager.lvlTileWidth) * LevelManager.tilePixelWidth,
                    LevelManager.tilePixelHeight / 2 + (index / LevelManager.lvlTileWidth) * LevelManager.tilePixelHeight, 5);
            shapeRenderer.end();

            if (priorNode != null) {
                int index2 = priorNode.getIndex();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(LevelManager.tilePixelWidth / 2 + (index % LevelManager.lvlTileWidth) * LevelManager.tilePixelWidth,
                        LevelManager.tilePixelHeight / 2 + (index / LevelManager.lvlTileWidth) * LevelManager.tilePixelHeight,
                        LevelManager.tilePixelWidth / 2 + (index2 % LevelManager.lvlTileWidth) * LevelManager.tilePixelWidth,
                        LevelManager.tilePixelHeight / 2 + (index2 / LevelManager.lvlTileWidth) * LevelManager.tilePixelHeight);
                shapeRenderer.end();
            }

            priorNode = node;
        }
    }
}
