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

import oauth2.entities.User

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.crypto.password.NoOpPasswordEncoder

import spock.lang.Specification

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
