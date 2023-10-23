package NotBox2D

enum EventEnum(event:String) {
    /**
     * 发生碰撞的事件
     */
    case CONTACT extends EventEnum("contact")

}