package com.mygdx.game.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;


public class Node implements IndexedNode<Node> {
    private Array<Connection<Node>> connections = new Array<Connection<Node>>();
    public int type;
    public int index;

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Array<Connection<Node>> getConnections() {
        return connections;
    }

    public void createConnection(Node toNode, float cost) {
        connections.add(new ConnectionImp(this, toNode, cost));
    }

    public static class Type {
        public static final int REGULAR = 1;
    }
}
