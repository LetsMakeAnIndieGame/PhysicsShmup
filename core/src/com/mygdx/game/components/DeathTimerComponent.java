package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.PhysicsShmup;

<<<<<<< HEAD

=======
/**
 * Created by Phil on 2/21/2015.
 */
>>>>>>> 437872d6f8d44f9dc3ffe938a1dca805f6282a1d
public class DeathTimerComponent implements Component {
    public long createTime;
    public long deathTime;

    public DeathTimerComponent(long deathTime) {
        createTime = PhysicsShmup.currentTimeMillis;
        this.deathTime = deathTime;
    }
}
