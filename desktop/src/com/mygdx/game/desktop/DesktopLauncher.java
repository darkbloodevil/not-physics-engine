package com.mygdx.game.desktop;

import NotBox2D.JsonReader;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.NotPhysicsEngineMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=1080;
		config.height=720;
		new LwjglApplication(new NotPhysicsEngineMain(), config);
	}
}
