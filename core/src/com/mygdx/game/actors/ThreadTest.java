package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.mygdx.game.pathfinding.FlyingHeuristic;
import com.mygdx.game.pathfinding.GraphPathImp;
import com.mygdx.game.pathfinding.Node;

public class ThreadTest implements Runnable {
    Thread thread = null;
    Node startNode, endNode;
    IndexedAStarPathFinder<Node> pathFinder;
    GraphPathImp resultPath;

    @Override
    public void run() {
        while (true) {
            int x = 1000;
            while (--x > 0) {
                resultPath = new GraphPathImp();
                pathFinder.searchNodePath(startNode, endNode, new FlyingHeuristic(), resultPath);
            }
        }
    }

    public void start(IndexedAStarPathFinder<Node> pathFinder, Node startNode, Node endNode) {
        resultPath = new GraphPathImp();
        this.startNode = startNode;
        this.endNode = endNode;
        this.pathFinder = pathFinder;

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
}
