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
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.components.physics.PositionComponent;
import com.mygdx.game.pathfinding.*;
import com.mygdx.managers.EntityManager;
import com.mygdx.managers.LevelManager;
import com.mygdx.managers.Messages;

/**
 * Created by Phil on 4/11/2015.
 */
public class FlyingTestEnemyComponent extends Component implements Telegraph, Updateable {
    public StateMachine<FlyingTestEnemyComponent> stateMachine;

    public boolean isShot = false;

    private Entity entity;
    private Steering steering;
    private Steering waypoint;

    private IndexedAStarPathFinder<Node> pathFinder;
    private GraphPathImp resultPath = new GraphPathImp();

    public FlyingTestEnemyComponent(Entity entity, Steering steering) {
        this.entity = entity;
        this.steering = steering;
        stateMachine = new DefaultStateMachine<FlyingTestEnemyComponent>(this, FlyingEnemyState.SEEKING);
        MessageManager.getInstance().addListener(this, Messages.PLAYER_ATTACKED_ENEMY);

        pathFinder = new IndexedAStarPathFinder<Node>(LevelManager.airGraph, false);

        int startX = (int) steering.getPosition().x;
        int startY = (int) steering.getPosition().y;

        Entity player = EntityManager.getPlayer();
        PositionComponent playerPos = player.getComponent(PositionComponent.class);
        int endX = (int) playerPos.x;
        int endY = (int) playerPos.y;

        Node startNode = LevelManager.airGraph.getNodeByXY(startX, startY);
        Node endNode = LevelManager.airGraph.getNodeByXY(endX, endY);

        pathFinder.searchNodePath(startNode, endNode, new FlyingHeuristic(), resultPath);

        try {
            int waypointIndex = resultPath.get(0).getIndex();
            waypoint = new PointSteering(new Vector2(waypointIndex % LevelManager.lvlTileWidth * LevelManager.tilePixelWidth + 25, waypointIndex / LevelManager.lvlTileWidth * LevelManager.tilePixelHeight + 25));
            steering.setTarget(waypoint);
        } catch(Exception e) {
            //do nothing
        }
    }

    public void update(float delta) {
        stateMachine.update();

        // Change this to time based
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && steering.getLinearVelocity().x == 0 && steering.getLinearVelocity().y == 0) {
            try {
                resultPath.removeIndex(0);
                int waypointIndex = resultPath.get(0).getIndex();
                waypoint = new PointSteering(new Vector2(waypointIndex % LevelManager.lvlTileWidth * LevelManager.tilePixelWidth + 25, waypointIndex / LevelManager.lvlTileWidth * LevelManager.tilePixelHeight + 25));
                steering.setTarget(waypoint);
                steering.setSteeringBehavior(Arrive.class);
            } catch(Exception e) {
                // do nothing
            }
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
