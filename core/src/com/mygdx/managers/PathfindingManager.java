package com.mygdx.managers;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.pfa.PathFinderQueue;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.sched.LoadBalancingScheduler;
import com.mygdx.game.pathfinding.FlyingHeuristic;
import com.mygdx.game.pathfinding.GraphPathImp;
import com.mygdx.game.pathfinding.Node;
import com.mygdx.game.pathfinding.Pather;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class PathfindingManager {

    private static PathfindingManager instance = null;

    private static int threadCount = 0;
    public static CopyOnWriteArrayList<PathfindingThread> activeThreads = new CopyOnWriteArrayList<>();

    private PathfindingManager() {
        threadCount = Runtime.getRuntime().availableProcessors();

        threadCount -= 2;
    }

    public void requestPathfinding(Pather requester, IndexedAStarPathFinder<Node> pathFinder,
                                   Node startNode, Node endNode) {
        if (activeThreads.size() < threadCount) {
            PathfindingThread thread = new PathfindingThread();
            activeThreads.add(thread);

            thread.addRequestToQueue(requester, startNode, endNode);

            thread.start();
        } else {
            int threadQueueSize = activeThreads.get(0).queue.size();
            PathfindingThread firstShortest = activeThreads.get(0);

            Iterator<PathfindingThread> iterator = activeThreads.iterator();

            while (iterator.hasNext()) {
                PathfindingThread currentThread = iterator.next();

                if (currentThread.queue.size() < threadQueueSize) {
                    firstShortest = currentThread;
                } else {
                    threadQueueSize = currentThread.queue.size();
                }
            }

            firstShortest.addRequestToQueue(requester, startNode, endNode);
        }
    }

    public static void releasePathfindingThread(PathfindingThread pathfindingThread) {
        pathfindingThread.thread.interrupt();
        activeThreads.remove(pathfindingThread);
    }

    public static PathfindingManager getInstance() {
        if (instance == null) {
            instance = new PathfindingManager();
        }

        return instance;
    }

    public class PathfindingThread implements Runnable {
        public Thread thread = null;

        public PathFinderQueue<Node> queue;
        LoadBalancingScheduler scheduler = new LoadBalancingScheduler(60);

        CopyOnWriteArrayList<Pather> requesterList = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<PathFinderRequest<Node>> pathfinderRequests = new CopyOnWriteArrayList<>();
        MessageDispatcher dispatcher = new MessageDispatcher();

        IndexedAStarPathFinder<Node> pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.airGraph);

        public PathfindingThread() {
            queue = new PathFinderQueue<>(pathFinder);
            dispatcher.addListener(queue, Messages.REQUEST_PATHFINDING);

            scheduler.addWithAutomaticPhasing(queue, 1); // change if you add more queues
        }

        public void addRequestToQueue(Pather requester, Node startNode, Node endNode) {
            GraphPathImp resultPath = new GraphPathImp();
            PathFinderRequest<Node> request = new PathFinderRequest<>(startNode, endNode, new FlyingHeuristic(), resultPath);
            dispatcher.dispatchMessage(Messages.REQUEST_PATHFINDING, request);
            pathfinderRequests.add(request);
            requesterList.add(requester);
        }

        @Override
        public void run() {
            while (true) {
                scheduler.run(9999999);

                Iterator<PathFinderRequest<Node>> requestIterator = pathfinderRequests.iterator();

                while(requestIterator.hasNext()) {
                    PathFinderRequest<Node> request = requestIterator.next();

                    if (request.status == PathFinderRequest.SEARCH_FINALIZED) {
                        int index = pathfinderRequests.indexOf(request);
                        Pather requester = requesterList.get(index);

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                requester.handlePath((GraphPathImp) request.resultPath, request.pathFound);
                            }
                        });

                        requesterList.remove(index);
                        pathfinderRequests.remove(request);
                    }
                }
                if (queue.size() == 0) {
                    break;
                }
            }

            dispatcher.removeListener(queue, Messages.REQUEST_PATHFINDING);

            PathfindingManager.releasePathfindingThread(this);
        }

        public void start() {
            if (thread == null) {
                thread = new Thread(this);
                thread.setDaemon(true);
                thread.start();
            }
        }
    }
}
