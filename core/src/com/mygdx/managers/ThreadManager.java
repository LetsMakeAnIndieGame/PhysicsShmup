package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.PathFinderQueue;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.PathFinderRequestControl;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.sched.LoadBalancingScheduler;
import com.badlogic.gdx.ai.sched.Schedulable;
import com.badlogic.gdx.ai.sched.SchedulerBase;
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

        PathFinderQueue<Node> queue;
        PathFinderRequest<Node> request;
        LoadBalancingScheduler scheduler = new LoadBalancingScheduler(60);

        @Override
        public void run() {
            queue = new PathFinderQueue<>(pathFinder);
            MessageManager messageManager = MessageManager.getInstance();
            messageManager.addListener(queue, Messages.REQUEST_PATHFINDING);
            request = new PathFinderRequest<>(startNode, endNode, new FlyingHeuristic(), resultPath);
            messageManager.dispatchMessage(Messages.REQUEST_PATHFINDING, request);

            scheduler.addWithAutomaticPhasing(queue, 2);

            while (true) {
                scheduler.run(5000);

                if (request.statusChanged) {
                    Gdx.app.log("Status changed", "" + request.status);

                    if (request.status == PathFinderRequest.SEARCH_FINALIZED) {
                        if (request.pathFound) {
                            Gdx.app.log("Path result", "Found");
                        } else {
                            Gdx.app.log("Path result", "Not Found");
                        }
                        Gdx.app.log("Search finished", "" + request.resultPath.getCount());
                        break;
                    }
                }

//                int x = 1000;
//                while (--x > 0) {
//                    resultPath = new GraphPathImp();
//                    pathFinder.searchNodePath(startNode, endNode, new FlyingHeuristic(), resultPath);
//                }
//
//                ThreadManager.releasePathfindingThread(this);
//                break;
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
