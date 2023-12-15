package exceptions;

public class JsonInheritanceException extends Exception {
    public JsonInheritanceException() {
        super();
    }

    public JsonInheritanceException(String message) {
        super(message);
    }

    public JsonInheritanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonInheritanceException(Throwable cause) {
        super(cause);
    }
}
