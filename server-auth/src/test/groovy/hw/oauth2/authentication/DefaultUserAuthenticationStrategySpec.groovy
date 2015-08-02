package hw.oauth2.authentication

import java.time.Instant

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.crypto.password.NoOpPasswordEncoder

import spock.lang.Specification

import hw.oauth2.entities.User

class DefaultUserAuthenticationStrategySpec extends Specification {

    static String USER_ID = "a user id"
    static String PASSWORD = "password"

    User user = new User(userId: USER_ID, password: PASSWORD, passwordExpiresAt: Instant.now().plusSeconds(3600))

    UserAuthenticationStrategy authenticationStrategy = new DefaultUserAuthenticationStrategy(NoOpPasswordEncoder.INSTANCE)

    def "if user is not enabled, a DisabledException exception will be thrown"() {
        given:
        user.password = null

        when:
        authenticationStrategy.authenticate(user, PASSWORD)

        then:
        thrown(DisabledException)
    }

    def "if user account is locked, a LockedException exception will be thrown"() {
        given:
        while (!user.accountLocked) {
            user.loginStatus.loginFailed(Instant.now())
        }

        when:
        authenticationStrategy.authenticate(user, PASSWORD)

        then:
        thrown(LockedException)
    }

    def "if no password is passed, a BadCredentials exception will be thrown"() {
        given:
        String missingPassword = null

        when:
        authenticationStrategy.authenticate(user, missingPassword)

        then:
        thrown(BadCredentialsException)
    }

    def "if the password does match, a BadCredentials exception will be thrown"() {
        given:
        String wrongPassword = "wrong password"

        when:
        authenticationStrategy.authenticate(user, wrongPassword)

        then:
        thrown(BadCredentialsException)
    }

    def "Successful authentication"() {

        when:
        authenticationStrategy.authenticate(user, PASSWORD)

        then:
        // UserAuthenticationStrategy#authenticate returns normally
        true
    }
}
