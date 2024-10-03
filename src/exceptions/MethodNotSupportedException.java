package exceptions;

public class MethodNotSupportedException extends TokenException {
    public MethodNotSupportedException(String message) {
        super(message);
    }
    public MethodNotSupportedException() { this(""); }
}
