package java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import game.NotPhysicsEngineGUI;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=1600;
		config.height=900;
		//config.fullscreen=true;
		new LwjglApplication(new NotPhysicsEngineMain(), config);
	}
}
