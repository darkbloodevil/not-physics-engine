package systems

import com.badlogic.ashley.core._
import com.badlogic.ashley.core._
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.utils.Array
import java.util


object PhysicsSystem {
    var PHYSICS_WORLD: World = null
    //float kind message needs to add an F
    val MESSAGE_APPLY_FORCE = "apply_force"
    val MESSAGE_END = "end"
    val MESSAGE_DIRECTION_X = "direction_x"
    val MESSAGE_DIRECTION_Y = "direction_y"
    val MESSAGE_POSITION_X = "position_x"
    val MESSAGE_POSITION_Y = "position_y"
    val MESSAGE_STRENGTH = "strength"
    private val TIME_STEP = .005f
    private val VELOCITY_ITERATIONS = 8
    private val POSITION_ITERATIONS = 10
    var GRAVITY = 0
}

class PhysicsSystem(var engine: Engine) extends EntitySystem {
    PhysicsSystem.PHYSICS_WORLD = new World(new Vector2(0, PhysicsSystem.GRAVITY), true)


    override def addedToEngine(engine: Engine): Unit = {
        super.addedToEngine(engine)
    }

    override def update(deltaTime: Float): Unit = {
        super.update(deltaTime)

    }


}
