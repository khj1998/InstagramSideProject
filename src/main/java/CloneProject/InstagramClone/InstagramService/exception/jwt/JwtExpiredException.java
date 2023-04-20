package CloneProject.InstagramClone.InstagramService.exception.jwt;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
