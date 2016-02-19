package com.mygdx.game.actors;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;


public interface Updateable {
    public void update(float timeDelta);
}
