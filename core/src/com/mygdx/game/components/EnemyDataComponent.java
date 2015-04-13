package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Phil on 2/19/2015.
 */
public class EnemyDataComponent extends Component {
    public long level = 1;
    public long health = 100;
    public long maxHealth = 100;
    public long shield = 0;
    public long maxShield = 0;
}
