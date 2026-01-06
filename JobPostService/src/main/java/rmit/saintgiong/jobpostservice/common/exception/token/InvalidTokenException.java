package rmit.saintgiong.jobpostservice.common.exception.token;

// Exception thrown when a token is invalid or malformed.
public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
