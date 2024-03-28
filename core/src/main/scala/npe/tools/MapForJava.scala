package npe.tools

import scala.jdk.CollectionConverters._

/**
 * 适配java和scala的map
 *
 * @tparam k key
 * @tparam v value
 */
class MapForJava[k, v] {
    private var map: Map[k, v] = Map()
    
    def put(key: k, value: v): Unit = {
        map += (key -> value)
    }
    
    def keys(): java.util.List[k] = {
        map.keySet.toList.asJava
    }
    
    def get(key: k): v = {
        map.apply(key)
    }
    
    def remove(key: k): Unit = {
        map = map.removed(key)
    }
    
    def clear(): Unit = {
        map = Map()
    }
    
}
