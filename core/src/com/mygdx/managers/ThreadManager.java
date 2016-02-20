package com.mygdx.managers;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.mygdx.game.pathfinding.FlyingHeuristic;
import com.mygdx.game.pathfinding.GraphPathImp;
import com.mygdx.game.pathfinding.Node;

import java.util.ArrayList;

public class ThreadManager {

    private static ThreadManager instance = null;

    private static int threadCount = 0;
    private static ArrayList<PathfindingThread> activeThreads = new ArrayList<>();

    private ThreadManager() {
        threadCount = Runtime.getRuntime().availableProcessors();

        threadCount--;
    }

    public PathfindingThread requestPathfindingThread() {
        if (activeThreads.size() < threadCount) {
            PathfindingThread thread = new PathfindingThread();
            activeThreads.add(thread);

            return thread;
        } else {
            return null;
        }
    }

    public static void releasePathfindingThread(PathfindingThread pathfindingThread) {
        activeThreads.remove(pathfindingThread);
    }

    public static ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }

        return instance;
    }

    public class PathfindingThread implements Runnable {
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

                ThreadManager.releasePathfindingThread(this);
                break;
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
}
