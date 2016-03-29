package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.PhysicsShmup;
import com.badlogic.gdx.utils.Pool.Poolable;


public class DeathTimerComponent implements Component, Poolable {
    public long createTime = 0;
    public long deathTime = 0;

    public DeathTimerComponent() {}

    @Override
    public void reset() {
        createTime = 0;
        deathTime = 0;
    }

    public DeathTimerComponent(long deathTime) {
        createTime = PhysicsShmup.currentTimeMillis;
        this.deathTime = deathTime;
    }
}
