package npe.tools

import scala.jdk.CollectionConverters._

/**
 * 适配java和scala的map
 *
 * @tparam k key
 * @tparam v value
 */
class MapForJava[k, v] {
    private[this] var _map: Map[k, v] = Map()
    
    def get_map: Map[k, v] = _map
    
    def put(key: k, value: v): Unit = {
        _map += (key -> value)
    }
    
    def keys(): java.util.List[k] = {
        _map.keySet.toList.asJava
    }
    
    def get(key: k): v = {
        _map.apply(key)
    }
    
    def remove(key: k): Unit = {
        _map = _map.removed(key)
    }
    
    def clear(): Unit = {
        _map = Map()
    }
    
}
