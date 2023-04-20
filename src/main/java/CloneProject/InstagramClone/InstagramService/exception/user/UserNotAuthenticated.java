package CloneProject.InstagramClone.InstagramService.exception.user;

public class UserNotAuthenticated extends RuntimeException{
    public UserNotAuthenticated(String message) {
        super(message);
    }
}
