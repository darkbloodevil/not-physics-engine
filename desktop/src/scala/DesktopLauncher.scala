import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import game.{NotPhysicsEngineCLI, NotPhysicsEngineGUI}
import org.json.*
import tools.JsonReader
import org.slf4j.LoggerFactory

object DesktopLauncher {
    def main(args: Array[String]): Unit = {
        val logger = LoggerFactory.getLogger(classOf[DesktopLauncher])
        val interface_type = args.apply(0)
        logger.info("interface_type:" + interface_type)
        if (interface_type.equals("cli")) {
            // cli界面
            val aaa = new NotPhysicsEngineCLI
//            aaa.npec()
        }
        else if (interface_type.equals("gui")) {
            // gui界面
            val config = new LwjglApplicationConfiguration
            val settings_json = JsonReader.read_json_from_path("json_files/settings.json")
            val desktop_setting = settings_json.getJSONObject("desktop_config")
            config.width = desktop_setting.getInt("width");
            config.height = desktop_setting.getInt("height");
            new LwjglApplication(new NotPhysicsEngineGUI, config)
        }

    }
}

class DesktopLauncher {

}