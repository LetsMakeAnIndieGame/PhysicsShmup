package com.mygdx.game.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.managers.LevelManager;

import java.util.Iterator;

/**
 * Created by Phil on 4/3/2015.
 */
public class PathfindingDebugger {
    private static OrthographicCamera camera;
    private static ShapeRenderer shapeRenderer;

    public static void setCamera(OrthographicCamera camera) {
        PathfindingDebugger.camera = camera;
        shapeRenderer = new ShapeRenderer();
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
