package components
import com.artemis.Component
import com.artemis.Entity
/**
 * 挂钩实体的component
 * 带有change_entity使得更改实体的component时候可以改变依赖的实体（需要子类中实现
 */
class LinkedComponent extends Component{
    /**
     * 改变component的entity
     * @param entity 新的entity
     */
    def change_entity(entity:Entity): Unit = {
        
    }
}
