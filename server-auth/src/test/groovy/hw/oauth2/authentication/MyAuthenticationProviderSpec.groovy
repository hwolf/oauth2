package hw.oauth2.authentication

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

import hw.oauth2.Roles
import hw.oauth2.entities.User
import hw.oauth2.entities.UserRepository

class MyAuthenticationProviderSpec extends Specification {

    static String USER_ID = "a user id"
    static String PASSWORD = "password"

    User user = new User(userId: USER_ID, password: PASSWORD, passwordExpiresAt: Instant.now().plusSeconds(3600))

    UserRepository userRepository = Mock() { findByUserId(USER_ID) >> user }
    UserAuthenticationStrategy authenticationStrategy = new DefaultUserAuthenticationStrategy(NoOpPasswordEncoder.INSTANCE)
    MyAuthenticationProvider authenticationProvider = new MyAuthenticationProvider(userRepository, authenticationStrategy)

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
        user.password = null

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        thrown(DisabledException)
    }

    def "if user account is locked, a LockedException exception will be thrown"() {
        given:
        while (!user.accountLocked) {
            user.loginStatus.loginFailed(Instant.now())
        }

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        thrown(LockedException)
    }

    def "if no password is passed, a BadCredentials exception will be thrown"() {
        given:
        String missingPassword = null

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, missingPassword))

        then:
        thrown(BadCredentialsException)
    }

    def "if the password does match, a BadCredentials exception will be thrown"() {
        given:
        String wrongPassword = "wrong password"

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, wrongPassword))

        then:
        thrown(BadCredentialsException)
    }

    def "if the password is expired, the user is authenticated successful but has only role MUST_PASSWORD_CHANGE"() {
        given:
        user.passwordExpiresAt = Instant.now()

        when:
        Authentication authentication =
                authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        authentication.authenticated
        authentication.authorities.authority == [
            "ROLE_" + Roles.MUST_CHANGE_PASSWORD
        ]
    }

    def "if the password is not expired, the user is authenticated successful"() {

        when:
        Authentication authentication =
                authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        authentication.authenticated
        !authentication.authorities.authority.contains("ROLE_" + Roles.MUST_CHANGE_PASSWORD)
    }

    def "check login status after login failed"() {

        given:
        user.loginStatus.failedLoginAttempts = 1

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, "wrong password"))

        then:
        thrown(BadCredentialsException)
        user.loginStatus.failedLoginAttempts == 2
        !user.loginStatus.lastFailedLogin.isAfter(Instant.now())
    }

    def "check login status after successful login"() {

        given:
        user.loginStatus.failedLoginAttempts = 1

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        user.loginStatus.failedLoginAttempts == 0
        !user.loginStatus.lastSuccessfulLogin.isAfter(Instant.now())
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
