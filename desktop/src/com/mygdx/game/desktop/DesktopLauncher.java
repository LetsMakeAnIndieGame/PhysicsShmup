package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.PhysicsShmup;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		int fps = 3000;
		config.width = 1280;
		config.height = 720;
//		config.fullscreen = true;
		config.foregroundFPS = fps;
		config.backgroundFPS = fps;
		config.vSyncEnabled = true;
		new LwjglApplication(new PhysicsShmup(), config);
		}
}
