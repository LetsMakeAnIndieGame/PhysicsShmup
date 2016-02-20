package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class PlayerDataComponent implements Component {
    public long level = 1;
    public long money = 0;
    public long health = 100;
    public long maxHealth = 100;
    public long shield = 0;
    public long maxShield = 0;
}
