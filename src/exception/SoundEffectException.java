package exception;

public class SoundEffectException extends Exception {
    public SoundEffectException(String message) {
        super("Exception encountered when processing audio file. Exception: " + message);
    }
}
