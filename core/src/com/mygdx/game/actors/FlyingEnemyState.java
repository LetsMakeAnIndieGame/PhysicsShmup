package com.mygdx.game.actors;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.managers.Messages;

public enum FlyingEnemyState implements State<FlyingTestEnemyComponent> {

    SEEKING() {
        @Override
        public void update(FlyingTestEnemyComponent flyingEnemyComponent) {
            if (flyingEnemyComponent.isShot) {
                MessageManager.getInstance().dispatchMessage(0, flyingEnemyComponent, Messages.PLAYER_ATTACKED_ENEMY);
                flyingEnemyComponent.stateMachine.changeState(FlyingEnemyState.RUNNING);
            }
        }
    },

    RUNNING() {
        @Override
        public void update(FlyingTestEnemyComponent flyingEnemyComponent) {
            // this state never ends
        }
    };

    @Override
    public void enter(FlyingTestEnemyComponent flyingEnemyComponent) {
        if (flyingEnemyComponent.stateMachine.getCurrentState() == FlyingEnemyState.SEEKING) {
            flyingEnemyComponent.startSeeking();
        } else if (flyingEnemyComponent.stateMachine.getCurrentState() == FlyingEnemyState.RUNNING) {
            flyingEnemyComponent.startRetreating();
        }
    }

    @Override
    public void exit(FlyingTestEnemyComponent flyingEnemyComponent) {}

    @Override
    public boolean onMessage(FlyingTestEnemyComponent flyingEnemyComponent, Telegram telegram) {
        // ignore telegrams for now
        return false;
    }
}
