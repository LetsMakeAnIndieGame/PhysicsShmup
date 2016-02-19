package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

// This contains generalized functions of all game entities (to help EntityManager)
public interface GameObject {
    public void dispose();

    public void update();

    public void render(Batch batch);
}
