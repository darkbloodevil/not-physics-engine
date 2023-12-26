import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import game.com.mygdx.NotPhysicsEngineMain;
import org.json._
import tools.JsonReader

object DesktopLauncher {
    def main(args: Array[String]): Unit = {
        //        println(PhysicsSystem.PHYSICS_SYSTEM_TOKEN)

        val config = new LwjglApplicationConfiguration
        val settings_json=JsonReader.read_json_from_path("json_files/settings.json")
        val desktop_setting=settings_json.getJSONObject("desktop_config")
        config.width=desktop_setting.getInt("width");
        config.height=desktop_setting.getInt("height");
//        config.width = 1600
//        config.height = 900
        //config.fullscreen=true;
        new LwjglApplication(new NotPhysicsEngineMain, config)
    }
}
