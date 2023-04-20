package CloneProject.InstagramClone.InstagramService.exception.jwt;

public class JwtSignatureException extends RuntimeException{
    public JwtSignatureException(String message) {
        super(message);
    }
}
