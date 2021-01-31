package exception;

public class InvalidMapException extends Exception {
    public InvalidMapException(String message) {
        super("Malformed map provided: " + message);
    }
}
