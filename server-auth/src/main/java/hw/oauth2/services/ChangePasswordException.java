package hw.oauth2.services;

public class ChangePasswordException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ChangePasswordException(String message) {
        super(message);
    }
}
