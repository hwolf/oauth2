package hw.oauth2.entities.user

import hw.oauth2.entities.User

import java.time.Instant
import java.time.temporal.ChronoUnit

import spock.lang.Specification

class UserSpec extends Specification {

    def "when the password expires date is in the future, then the password is not expired"() {
        given:
        Instant passwordExpiresAt = Instant.now().plus(1, ChronoUnit.MINUTES)

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        !user.passwordExpired
    }

    def "when the password expires date is in the past, then the password is expired"() {
        given:
        Instant passwordExpiresAt = Instant.now()

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        user.passwordExpired
    }

    def "when no password expires date is given, then the password is expired"() {
        given:
        Instant passwordExpiresAt = null

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        user.passwordExpired
    }

    def "when a password is given, then the user is enabled"() {
        given:
        String password = "given password"

        when:
        User user = new User (password: password)

        then:
        user.enabled
    }

    def "when no password is given, then the user is not enabled"() {
        given:
        String password = ""

        when:
        User user = new User(password: password)

        then:
        !user.enabled
    }

    def "with 2 failed login attempts, the account is not locked"() {
        given:
        int failedLogins = 2

        when:
        User user = new User()
        user.loginStatus.failedLoginAttempts = failedLogins

        then:
        !user.accountLocked
    }

    def "with 3 failed login attempts, the account is locked"() {
        given:
        int failedLogins = 3

        when:
        User user = new User()
        user.loginStatus.failedLoginAttempts = failedLogins

        then:
        user.accountLocked
    }
}
