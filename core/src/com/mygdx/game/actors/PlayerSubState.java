package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.PhysicsShmup;
import com.mygdx.game.components.fsm.PlayerAgent;
import com.mygdx.managers.PhysicsManager;

public enum PlayerSubState implements State<PlayerAgent> {

    NONE() {
        @Override
        public void update(PlayerAgent playerAgent) {

        }
    },

    WALL_SLIDING() {
        @Override
        public void update(PlayerAgent playerAgent) {
            if (playerAgent.getBodyVelocityY() < -100 * PhysicsManager.PIXELS_TO_METERS ||
                    (!playerAgent.isTouchingWallRight && !playerAgent.isTouchingWallLeft)) {
                playerAgent.subStateMachine.changeState(PlayerSubState.NONE);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                playerAgent.wallJump(60, 30);

                playerAgent.lastWallJump = PhysicsShmup.currentTimeMillis;
                playerAgent.hasWallSlid = false;
                playerAgent.subStateMachine.changeState(PlayerSubState.WALL_JUMPED);
            }
        }
    },

    WALL_JUMPED() {
        @Override
        public void update(PlayerAgent playerAgent) {
            if (PhysicsShmup.currentTimeMillis - playerAgent.lastWallJump >= 500) {
                playerAgent.subStateMachine.changeState(PlayerSubState.NONE);
            }
        }
    };

    @Override
    public void enter(PlayerAgent playerAgent) {}

    @Override
    public void exit(PlayerAgent playerAgent) {}

    @Override
    public boolean onMessage(PlayerAgent playerAgent, Telegram telegram) {
        // ignore telegrams for now
        return false;
    }
}
