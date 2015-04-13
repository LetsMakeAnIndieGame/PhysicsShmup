package com.mygdx.game.actors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.mygdx.game.pathfinding.GraphPathImp;
import com.mygdx.game.pathfinding.HeuristicImp;
import com.mygdx.game.pathfinding.Node;
import com.mygdx.game.pathfinding.PathfindingDebugger;
import com.mygdx.managers.LevelManager;
import com.mygdx.managers.Messages;

/**
 * Created by Phil on 4/11/2015.
 */
public class FlyingTestEnemyComponent extends Component implements Telegraph, Updateable {
    public StateMachine<FlyingTestEnemyComponent> stateMachine;

    public boolean isShot = false;

    private Entity entity;
    private TestEnemySteering testEnemySteering;

    private IndexedAStarPathFinder<Node> pathFinder;
    private GraphPathImp resultPath = new GraphPathImp();

    public FlyingTestEnemyComponent(Entity entity, TestEnemySteering testEnemySteering) {
        this.entity = entity;
        this.testEnemySteering = testEnemySteering;
        stateMachine = new DefaultStateMachine<FlyingTestEnemyComponent>(this, FlyingEnemyState.SEEKING);
        MessageManager.getInstance().addListener(this, Messages.PLAYER_ATTACKED_ENEMY);

        pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.graph, false);

        int startX = (int) testEnemySteering.getPosition().x;
        int startY = (int) testEnemySteering.getPosition().y;

        int endX = (int) testEnemySteering.target.getPosition().x;
        int endY = (int) testEnemySteering.target.getPosition().y;

        Node startNode = LevelManager.graph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.graph.getNodeByXY(endX, endY);

        pathFinder.searchNodePath(startNode, endNode, new HeuristicImp(), resultPath);

        Gdx.app.log("Path length", "" + resultPath.getCount());
    }

    public void update(float delta) {
        stateMachine.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            resultPath.clear();

            int startX = (int) testEnemySteering.getPosition().x;
            int startY = (int) testEnemySteering.getPosition().y;

            int endX = (int) testEnemySteering.target.getPosition().x;
            int endY = (int) testEnemySteering.target.getPosition().y;

            Node startNode = LevelManager.graph.getNodeByXY(startX, startY);
            Node endNode = LevelManager.graph.getNodeByXY(endX, endY);

            pathFinder.searchNodePath(startNode, endNode, new HeuristicImp(), resultPath);
        }

        PathfindingDebugger.drawPath(resultPath);
    }

    public void startSeeking() {

        testEnemySteering.setSteeringBehavior(Arrive.class);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        if (telegram.message == Messages.PLAYER_ATTACKED_ENEMY) {
            isShot = true;

            return true;
        }

        return false;
    }

    public void startRetreating() {
        testEnemySteering.setSteeringBehavior(Flee.class);
    }
}
