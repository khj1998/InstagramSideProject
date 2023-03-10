package CloneProject.InstagramClone.InstagramService.exception;

public class UserNotAuthenticated extends RuntimeException{
    public UserNotAuthenticated(String message) {
        super(message);
    }
}
