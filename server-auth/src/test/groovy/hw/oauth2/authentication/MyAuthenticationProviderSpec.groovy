package hw.oauth2.authentication

import hw.oauth2.Roles
import hw.oauth2.entities.User
import hw.oauth2.entities.UserRepository

import java.time.Instant

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.NoOpPasswordEncoder

import spock.lang.Specification

class MyAuthenticationProviderSpec extends Specification {

    UserRepository userRepository = Mock()
    MyAuthenticationProvider authenticationProvider = new MyAuthenticationProvider(NoOpPasswordEncoder.INSTANCE, userRepository)

    static class MyUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {}

    def "supports UsernamePasswordAuthenticationToken"() {
        expect:
        authenticationProvider.supports(UsernamePasswordAuthenticationToken)
    }

    def "supports sub class of UsernamePasswordAuthenticationToken"() {
        expect:
        authenticationProvider.supports(MyUsernamePasswordAuthenticationToken)
    }

    def "does not support AbstractAuthenticationToken"() {
        expect:
        !authenticationProvider.supports(AbstractAuthenticationToken)
    }

    def "if user is not found, a BadCredentials exception will be thrown"() {
        given:
        String userId = "user doesn't exists"
        userRepository.findByUserId(userId) >> null

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, "password"))

        then:
        thrown(BadCredentialsException)
    }

    def "if user is not enabled, a DisabledException exception will be thrown"() {
        given:
        String userId = "a user id"
        userRepository.findByUserId(userId) >> new User(userId: userId, password: null)

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, "password"))

        then:
        thrown(DisabledException)
    }

    def "if user account is locked, a LockedException exception will be thrown"() {
        given:
        String userId = "a user id"
        User user = new User(userId: userId, password: "password")
        while (!user.accountLocked) {
            user.loginStatus.loginFailed(Instant.now())
        }
        userRepository.findByUserId(userId) >> user

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, "password"))

        then:
        thrown(LockedException)
    }

    def "if no password is passed, a BadCredentials exception will be thrown"() {
        given:
        String userId = "a user id"
        String passwordForAuthentifcation = null
        userRepository.findByUserId(userId) >> new User(userId: userId, password: "password")

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, passwordForAuthentifcation))

        then:
        thrown(BadCredentialsException)
    }

    def "if the password does match, a BadCredentials exception will be thrown"() {
        given:
        String userId = "a user id"
        userRepository.findByUserId(userId) >> new User(userId: userId, password: "password xxx")

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, "password xyz"))

        then:
        thrown(BadCredentialsException)
    }

    def "if the password is expired, the user is authenticated successful but has only role MUST_PASSWORD_CHANGE"() {
        given:
        String userId = "a user id"
        String password = "password"
        userRepository.findByUserId(userId) >> new User(userId: userId, password: password, passwordExpiresAt: Instant.now())

        when:
        Authentication authentication =
                authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, password))

        then:
        authentication.authenticated
        authentication.authorities.authority == [
            "ROLE_" + Roles.MUST_CHANGE_PASSWORD
        ]
    }

    def "if the password is not expired, the user is authenticated successful"() {
        given:
        String userId = "a user id"
        String password = "password"
        userRepository.findByUserId(userId) >> new User(userId: userId, password: password, passwordExpiresAt: Instant.now().plusSeconds(3600))

        when:
        Authentication authentication =
                authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, password))

        then:
        authentication.authenticated
        !authentication.authorities.authority.contains("ROLE_" + Roles.MUST_CHANGE_PASSWORD)
    }

    def "check login status after login failed"() {

        given:
        User user = new User()
        user.loginStatus.failedLoginAttempts = 1
        Instant when = Instant.now()

        when:
        authenticationProvider.loginFailedBecauseOfBadCredentials(user, when)

        then:
        user.loginStatus.failedLoginAttempts == 2
        user.loginStatus.lastFailedLogin == when
    }

    def "check login status after successful login"() {

        given:
        Authentication authentication = new UsernamePasswordAuthenticationToken("userId", "password")
        User user = new User()
        user.loginStatus.failedLoginAttempts = 1
        Instant when = Instant.now()

        when:
        authenticationProvider.loginSuccessful(authentication, user, [], when)

        then:
        user.loginStatus.failedLoginAttempts == 0
        user.loginStatus.lastSuccessfulLogin == when
    }

    def "check authentication object"() {

        given:
        String userId = "a user id"
        Collection<GrantedAuthority> authorities = [
            new SimpleGrantedAuthority("Authority 1")
        ]
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, "password")
        authentication.details = new Object()

        when:
        Authentication result = authenticationProvider.createSuccessAuthentication(userId, authentication, authorities)

        then:
        result.authenticated
        result.principal == authentication.principal
        result.authorities == authorities
        result.details == authentication.details
    }
}
