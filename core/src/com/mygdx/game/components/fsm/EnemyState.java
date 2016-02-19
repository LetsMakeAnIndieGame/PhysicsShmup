package com.mygdx.game.components.fsm;


import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.managers.Messages;


public enum EnemyState implements State<EnemyAgentComponent> {

    SEEKING() {
        @Override
        public void update(EnemyAgentComponent enemyAgentComponent) {
            if (enemyAgentComponent.isShot) {
                MessageManager.getInstance().dispatchMessage(0, enemyAgentComponent, Messages.PLAYER_ATTACKED_ENEMY);
                enemyAgentComponent.stateMachine.changeState(EnemyState.RUNNING);
            }
        }
    },

    RUNNING() {
        @Override
        public void update(EnemyAgentComponent enemyAgentComponent) {
            // this state never ends
        }
    };

    @Override
    public void enter(EnemyAgentComponent enemyAgentComponent) {
        if (enemyAgentComponent.stateMachine.getCurrentState() == EnemyState.SEEKING) {
            enemyAgentComponent.startSeeking();
        } else if (enemyAgentComponent.stateMachine.getCurrentState() == EnemyState.RUNNING) {
            enemyAgentComponent.startRetreating();
        }
    }

    @Override
    public void exit(EnemyAgentComponent enemyAgentComponent) {}

    @Override
    public boolean onMessage(EnemyAgentComponent enemyAgentComponent, Telegram telegram) {
        // ignore telegrams for now
        return false;
    }
}
