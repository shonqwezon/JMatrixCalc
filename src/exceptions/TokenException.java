package exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
    public TokenException() { this(""); }
}
