package components

import com.artemis.{Component, Entity}
import com.badlogic.gdx.physics.box2d.Body

/**
 * 用来记录body的component（就是为了用上component）
 *
 */
class PhysicsBodyComponent extends LinkedComponent {
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
     * 如果需要触发多次判定，只需设置该值为小于等于0即可
     */
    var contact_interval:Float=0.05f

    override def change_entity(entity: Entity): Unit = {
        super.change_entity(entity)
        this.body.setUserData(entity)
    }
}
