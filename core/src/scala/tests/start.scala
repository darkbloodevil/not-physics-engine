package tests

import com.badlogic.ashley.core.Engine
import game.com.mygdx.NotPhysicsEngineMain
import systems.PhysicsSystem

//import NotBox2D.JsonReader

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

object start {
    def main(args: Array[String]): Unit = {
//        println(PhysicsSystem.PHYSICS_SYSTEM_TOKEN)

        val config = new LwjglApplicationConfiguration
        config.width = 1600
        config.height = 900
        //config.fullscreen=true;
        new LwjglApplication(new NotPhysicsEngineMain, config)
    }
}
