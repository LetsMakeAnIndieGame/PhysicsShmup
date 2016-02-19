package com.mygdx.game.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.mygdx.managers.LevelManager;


public class HeuristicImp implements Heuristic<Node> {
    @Override
    public float estimate(Node startNode, Node endNode) {
        int startIndex = startNode.getIndex();
        int endIndex = endNode.getIndex();

        int startY = startIndex / LevelManager.lvlTileWidth;
        int startX = startIndex % LevelManager.lvlTileWidth;

        int endY = endIndex / LevelManager.lvlTileWidth;
        int endX = endIndex % LevelManager.lvlTileWidth;

        // magnitude of differences on both axes is Manhattan distance (not ideal)
        float distance = Math.abs(startX - endX) + Math.abs(startY - endY);

        return distance;
    }
}
