package components

import com.badlogic.ashley.core.Component


class StateComponent extends Component {
    private var state = 0
    var time = 0.0f

    def get: Int = state

    def set(newState: Int): Unit = {
        state = newState
        time = 0.0f
    }
}
