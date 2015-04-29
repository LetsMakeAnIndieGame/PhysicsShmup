package com.mygdx.game.components.fsm;

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
import com.mygdx.game.actors.Steering;
import com.mygdx.game.actors.Updateable;
import com.mygdx.game.pathfinding.GraphPathImp;
import com.mygdx.game.pathfinding.HeuristicImp;
import com.mygdx.game.pathfinding.Node;
import com.mygdx.game.pathfinding.PathfindingDebugger;
import com.mygdx.managers.LevelManager;
import com.mygdx.managers.Messages;

/**
 * Created by Phil on 2/28/2015.
 */
public class EnemyAgentComponent extends Component implements Telegraph, Updateable {
    public StateMachine<EnemyAgentComponent> stateMachine;

    public boolean isShot = false;

    private Entity entity;
    private Steering steering;

    private IndexedAStarPathFinder<Node> pathFinder;
    private GraphPathImp resultPath = new GraphPathImp();

    public EnemyAgentComponent(Entity entity, Steering steering) {
        this.entity = entity;
        this.steering = steering;
        stateMachine = new DefaultStateMachine<EnemyAgentComponent>(this, EnemyState.SEEKING);
        MessageManager.getInstance().addListener(this, Messages.PLAYER_ATTACKED_ENEMY);

        pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.groundGraph, false);

        int startX = (int) steering.getPosition().x;
        int startY = (int) steering.getPosition().y;

        int endX = (int) steering.getTarget().getPosition().x;
        int endY = (int) steering.getTarget().getPosition().y;

        Node startNode = LevelManager.groundGraph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.groundGraph.getNodeByXY(endX, endY);

        pathFinder.searchNodePath(startNode, endNode, new HeuristicImp(), resultPath);

        Gdx.app.log("Path length", "" + resultPath.getCount());
    }

    public void update(float delta) {
        stateMachine.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            resultPath.clear();

            int startX = (int) steering.getPosition().x;
            int startY = (int) steering.getPosition().y;

            int endX = (int) steering.getTarget().getPosition().x;
            int endY = (int) steering.getTarget().getPosition().y;

            Node startNode = LevelManager.groundGraph.getNodeByXY(startX, startY);
            Node endNode = LevelManager.groundGraph.getNodeByXY(endX, endY);

            pathFinder.searchNodePath(startNode, endNode, new HeuristicImp(), resultPath);
        }

        PathfindingDebugger.drawPath(resultPath);
    }

    public void startSeeking() {

        steering.setSteeringBehavior(Arrive.class);
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
        steering.setSteeringBehavior(Flee.class);
    }
}
