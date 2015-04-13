package com.mygdx.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.mygdx.managers.LevelManager;

/**
* Created by Phil on 3/8/2015.
*/
public class GraphImp extends DefaultIndexedGraph<Node> {
//    private Array<Node> nodes = new Array<Node>();

    public GraphImp() {
        super();
    }

    public GraphImp(int capacity) {
        super(capacity);
    }

    public GraphImp(Array<Node> nodes) {
        super(nodes);
    }


    public Node getNodeByXY(int x, int y) {
        int modX = x / LevelManager.tilePixelWidth;
        int modY = y / LevelManager.tilePixelHeight;

        return nodes.get(LevelManager.lvlTileWidth * modY + modX);
    }
}
