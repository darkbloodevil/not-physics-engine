package components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

/**
 * 用来记录body的component（就是为了用上component）
 *
 */
class PhysicsBodyComponent() extends Component {
    var body: Body = _
    var body_id: Long = 0

    /**
     * 用于表示物理系统的状态（这样子如果同时触发多个碰撞事件，就不会导致覆盖。
     */
    var physics_system_state: Any = _

    /**
     * 上次发生碰撞的时间
     */
    var last_contact:Float=0f

    /**
     * 用于表示碰撞检测的时间间隔（比如说撞一下，不可能触发多次判定）
     */
    var contact_interval:Float=0.05f
}
