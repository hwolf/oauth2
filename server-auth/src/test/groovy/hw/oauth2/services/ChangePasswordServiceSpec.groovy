package hw.oauth2.services

import hw.oauth2.entities.User
import hw.oauth2.entities.UserRepository

import java.time.Instant

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.validation.Errors

import spock.lang.Specification


class ChangePasswordServiceSpec extends Specification {

    static final USER_ID = "a user id"
    static final PASSWORD = "password"

    UserRepository userRepository = Mock()
    ChangePasswordService service = new ChangePasswordService(userRepository, NoOpPasswordEncoder.INSTANCE)

    def setup() {
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(USER_ID, PASSWORD, [])
    }

    def "If user id does not match with authentication token then signal an error"() {
        given:
        Errors errors = Mock()

        when:
        service.changePassword("other user id", PASSWORD, "new passsword", errors)

        then:
        1 * errors.reject("BadCredentials", _)
    }

    def "If user does not exists in user repository then signal an error"() {
        given:
        Errors errors = Mock()

        when:
        userRepository.findByUserId(USER_ID) >> null
        service.changePassword(USER_ID, PASSWORD, "new passsword", errors)

        then:
        1 * errors.reject("BadCredentials", _)
    }

    def "If user is locked in user repository then signal an error"() {
        given:
        Errors errors = Mock()

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        while (!user.accountLocked) {
            user.loginStatus.loginFailed(Instant.now())
        }
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, "new passsword", errors)

        then:
        1 * errors.reject("UserAccountLocked", _)
    }

    def "If user is not enabled in user repository then signal an error"() {
        given:
        Errors errors = Mock()

        when:
        userRepository.findByUserId(USER_ID) >> new User(userId: USER_ID, password: null)
        service.changePassword(USER_ID, PASSWORD, "new passsword", errors)

        then:
        1 * errors.reject("UserDisabled", _)
    }

    def "If password does not match with password from user repository then signal an error"() {
        given:
        Errors errors = Mock()

        when:
        userRepository.findByUserId(USER_ID) >> new User(userId: USER_ID, password: "another password")
        service.changePassword(USER_ID, PASSWORD, "new passsword", errors)

        then:
        1 * errors.reject("BadCredentials", _)
    }

    def "If all checks are passed then the password should changed"() {
        given:
        Errors errors = Mock()
        String newPassword = "new password"

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, newPassword, errors)

        then:
        user.password == newPassword
    }

    def "If all checks are passed then the passwordExpiresAt should be in the future "() {
        given:
        Errors errors = Mock()

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, "new password", errors)

        then:
        user.passwordExpiresAt.isAfter(Instant.now())
    }

    def "If all checks are passed then the SecurityContextHolder should be cleared"() {
        given:
        Errors errors = Mock()

        when:
        userRepository.findByUserId(USER_ID) >> new User(userId: USER_ID, password: PASSWORD)
        service.changePassword(USER_ID, PASSWORD, "new password", errors)

        then:
        SecurityContextHolder.context.authentication == null
    }
}
