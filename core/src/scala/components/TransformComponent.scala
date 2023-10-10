package components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.{Vector2, Vector3}

class TransformComponent extends Component {
    final val pos = new Vector3
    final val scale = new Vector2(1.0f, 1.0f)
    var rotation = 0.0f
}