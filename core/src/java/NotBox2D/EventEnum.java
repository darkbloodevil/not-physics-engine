package NotBox2D;

public enum EventEnum {
    CONTACT("CONTACT"),
    TIME_TRIGGER("TIME_TRIGGER");

    private final String event;

    EventEnum(String event) {
        this.event = event;
    }

    /**
     * 由string获取enum
     * @param value String value
     * @return 对应的枚举
     */
    public static EventEnum fromString(String value) {
        for (EventEnum e : EventEnum.values()) {
            if (e.event.equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }

}
