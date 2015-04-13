package com.mygdx.game.actors;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;

/**
 * Created by Phil on 2/24/2015.
 */
public interface Updateable {
    public void update(float timeDelta);
}
