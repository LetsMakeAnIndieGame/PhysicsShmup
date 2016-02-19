package com.mygdx.game.components.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.actors.PlayerSubState;
import com.mygdx.managers.GameInput;
import com.mygdx.managers.PhysicsManager;

public enum PlayerState implements State<PlayerAgent>  {

    GROUNDED() {
        @Override
        public void update(PlayerAgent playerAgent) {
            if (playerAgent.isTouchingGround)
                playerAgent.moveOnGround();
            else
                playerAgent.stateMachine.changeState(PlayerState.AIRBORN);
        }
    },

    AIRBORN() {
        @Override
        public void update(PlayerAgent playerAgent) {
            if ((GameInput.KeyForce.x < 0 && playerAgent.isTouchingWallLeft) || (GameInput.KeyForce.x > 0 && playerAgent.isTouchingWallRight)) {
                if (!playerAgent.hasWallSlid) {
                    if (playerAgent.subStateMachine.getCurrentState() != PlayerSubState.WALL_JUMPED) {
                        if (playerAgent.getBodyVelocityY() < 100 * PhysicsManager.PIXELS_TO_METERS) {
                            playerAgent.hasWallSlid = true;
                            playerAgent.subStateMachine.changeState(PlayerSubState.WALL_SLIDING);
                            playerAgent.setBodyVelocity(0, 0);
                        }
                    }
                }
            }

            if (!playerAgent.isTouchingGround) {
                if (playerAgent.subStateMachine.getCurrentState() != PlayerSubState.WALL_JUMPED)
                    playerAgent.moveInAir();
            } else {
                playerAgent.stateMachine.changeState(PlayerState.GROUNDED);
            }

        }
    };

    @Override
    public void enter(PlayerAgent playerAgent) {
        if (playerAgent.stateMachine.getCurrentState() == PlayerState.GROUNDED) {
            playerAgent.hasWallSlid = false;
            playerAgent.subStateMachine.changeState(PlayerSubState.NONE);
        }
    }

    @Override
    public void exit(PlayerAgent playerAgent) {}

    @Override
    public boolean onMessage(PlayerAgent playerAgent, Telegram telegram) {
        // ignore telegrams for now
        return false;
    }
}