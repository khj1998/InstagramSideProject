package CloneProject.InstagramClone.InstagramService.exception.user;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
