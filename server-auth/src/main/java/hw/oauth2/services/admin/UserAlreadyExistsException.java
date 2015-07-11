package hw.oauth2.services.admin;

public class UserAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(String message, Exception ex) {
        super(message, ex);
    }
}
