package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Phil on 1/16/2015.
 */

// This contains generalized functions all game entities (to help EntityManager
public interface GameObject {
    public void dispose();

    public void update();

    public void render(Batch batch);
}
