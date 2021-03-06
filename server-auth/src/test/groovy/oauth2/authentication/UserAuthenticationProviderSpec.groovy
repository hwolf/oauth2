/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.authentication

import java.time.Instant

import oauth2.Roles
import oauth2.entities.User
import oauth2.entities.UserRepository

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import spock.lang.Specification

class UserAuthenticationProviderSpec extends Specification {

    static String USER_ID = "a user id"
    static String PASSWORD = "password"

    User user = new User(userId: USER_ID, password: PASSWORD, passwordExpiresAt: Instant.now().plusSeconds(3600))

    UserAuthenticationStrategy authenticationStrategy = Mock()
    UserRepository userRepository = Mock() { findByUserId(USER_ID) >> user }
    UserAuthenticationProvider authenticationProvider = new UserAuthenticationProvider(userRepository, authenticationStrategy)

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

    def "if user is not authenticated, a AuthenticationException exception will be thrown"() {
        given:
        authenticationStrategy.authenticate(_, _) >> { throw new BadCredentialsException("BadCredentials") }

        when:
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        thrown(AuthenticationException)
    }

    def "if the password is expired, the user is authenticated successful but has only role MUST_PASSWORD_CHANGE"() {
        given:
        user.passwordExpiresAt = Instant.now()

        when:
        Authentication authentication =
                authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(USER_ID, PASSWORD))

        then:
        authentication.authenticated
        authentication.authorities.authority == ["ROLE_" + Roles.MUST_CHANGE_PASSWORD]
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
        authenticationStrategy.authenticate(_, _) >> { throw new BadCredentialsException("BadCredentials") }

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
