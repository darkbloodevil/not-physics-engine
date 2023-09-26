package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.game.com.mygdx.NotPhysicsEngineMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=1600;
		config.height=900;
		//config.fullscreen=true;
		new LwjglApplication(new NotPhysicsEngineMain(), config);
	}
}
