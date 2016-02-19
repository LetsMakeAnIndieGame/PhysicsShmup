package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.PhysicsShmup;


public class DeathTimerComponent implements Component {
    public long createTime;
    public long deathTime;

    public DeathTimerComponent(long deathTime) {
        createTime = PhysicsShmup.currentTimeMillis;
        this.deathTime = deathTime;
    }
}
