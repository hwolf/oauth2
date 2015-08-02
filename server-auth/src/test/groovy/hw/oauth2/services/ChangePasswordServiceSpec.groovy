package hw.oauth2.services

import java.time.Instant

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.NoOpPasswordEncoder

import spock.lang.Specification

import hw.oauth2.authentication.MyAuthenticationProvider
import hw.oauth2.entities.User
import hw.oauth2.entities.UserRepository


class ChangePasswordServiceSpec extends Specification {

    static final USER_ID = "a user id"
    static final PASSWORD = "password"

    UserRepository userRepository = Mock()
    MyAuthenticationProvider authenticationProvider = new MyAuthenticationProvider(NoOpPasswordEncoder.INSTANCE, userRepository)
    ChangePasswordService service = new ChangePasswordService(userRepository, authenticationProvider, NoOpPasswordEncoder.INSTANCE)

    def setup() {
        SecurityContextHolder.context.authentication = new TestingAuthenticationToken(USER_ID, PASSWORD, [])
    }

    def "If user id does not match with authentication token then a change password exception will be thrown"() {

        when:
        service.changePassword("other user id", PASSWORD, "new passsword")

        then:
        thrown(ChangePasswordException)
    }

    def "If user does not exists in user repository then a change password exception will be thrown"() {

        when:
        userRepository.findByUserId(USER_ID) >> null
        service.changePassword(USER_ID, PASSWORD, "new passsword")

        then:
        thrown(ChangePasswordException)
    }

    def "If user cannot authenticate then an authentication exception will be thrown"() {

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        while (!user.accountLocked) {
            user.loginStatus.loginFailed(Instant.now())
        }
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, "new passsword")

        then:
        thrown(AuthenticationException)
    }

    def "If all checks are passed then the password should changed"() {
        given:
        String newPassword = "new password"

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, newPassword)

        then:
        user.password == newPassword
    }

    def "If all checks are passed then the passwordExpiresAt should be in the future "() {

        when:
        User user = new User(userId: USER_ID, password: PASSWORD)
        userRepository.findByUserId(USER_ID) >> user
        service.changePassword(USER_ID, PASSWORD, "new password")

        then:
        user.passwordExpiresAt.isAfter(Instant.now())
    }

    def "If all checks are passed then the SecurityContextHolder should be cleared"() {

        when:
        userRepository.findByUserId(USER_ID) >> new User(userId: USER_ID, password: PASSWORD)
        service.changePassword(USER_ID, PASSWORD, "new password")

        then:
        SecurityContextHolder.context.authentication == null
    }
}
