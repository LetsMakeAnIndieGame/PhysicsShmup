package com.mygdx.game;

import com.badlogic.gdx.Gdx;


public class Time {
    public static double time = 1.0d; // Ratio of "idealized" framerate and actual framerate

    private static int defaultFPS = 60;

    public static void update() {
        int actualFPS = Gdx.graphics.getFramesPerSecond();
        actualFPS = (actualFPS == 0) ? 3000 : actualFPS;
        time = (double)defaultFPS / actualFPS;
    }
}
