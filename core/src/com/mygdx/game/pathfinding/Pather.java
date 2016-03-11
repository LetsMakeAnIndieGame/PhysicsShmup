package com.mygdx.game.pathfinding;

import com.badlogic.gdx.ai.pfa.PathFinderRequest;

public interface Pather<N> {
    public void acceptPath(PathFinderRequest<N> request);
}
