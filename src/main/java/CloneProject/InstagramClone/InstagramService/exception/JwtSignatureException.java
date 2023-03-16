package CloneProject.InstagramClone.InstagramService.exception;

public class JwtSignatureException extends RuntimeException{
    public JwtSignatureException(String message) {
        super(message);
    }
}
