package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.PhysicsShmup;

/**
 * Created by Phil on 2/21/2015.
 */
public class DeathTimerComponent implements Component {
    public long createTime;
    public long deathTime;

    public DeathTimerComponent(long deathTime) {
        createTime = PhysicsShmup.currentTimeMillis;
        this.deathTime = deathTime;
    }
}
