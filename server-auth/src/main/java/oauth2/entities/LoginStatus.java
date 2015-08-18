package oauth2.entities;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import oauth2.jpa.converters.InstantConverter;

@Entity
@Table(name = "t_login_status")
public class LoginStatus {

    @Id
    private String userId;

    private int failedLoginAttempts;

    @Convert(converter = InstantConverter.class)
    private Instant lastSuccessfulLogin;

    @Convert(converter = InstantConverter.class)
    private Instant lastFailedLogin;

    LoginStatus() {
    }

    public LoginStatus(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant getLastSuccessfulLogin() {
        return lastSuccessfulLogin;
    }

    public Instant getLastFailedLogin() {
        return lastFailedLogin;
    }

    public void loginSuccessful(Instant when) {
        failedLoginAttempts = 0;
        lastSuccessfulLogin = when;
    }

    public void loginFailed(Instant when) {
        failedLoginAttempts++;
        lastFailedLogin = when;
    }
}
