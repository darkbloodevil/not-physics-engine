package components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body

/**
 * 用来记录body的component（就是为了用上component）
 */
class PhysicsBodyComponent extends Component {
    var body: Body = _

}
