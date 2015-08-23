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
package oauth2.tests.login

import java.time.Instant

import oauth2.tests.OAuth2Spec
import oauth2.tests.pages.ChangePasswordPage
import oauth2.tests.pages.LoginPage
import oauth2.tests.pages.TestPage

class LoginSpec extends OAuth2Spec {

    def "A user account is locked after 3 failed logins"() {
        given:
        String userId = "aUser"
        String password = "password"

        userHelper.deleteUser(userId)
        userHelper.createUser(userId, password)

        when:
        to LoginPage

        then:
        noErrorMessage

        when: "First try with wrong password"
        login userId, "wrong password 1"

        then:
        errorMessage == 'Bad credentials'

        when: "Second try with wrong password"
        login userId, "wrong password 2"

        then:
        errorMessage == 'Bad credentials'

        when: "Third try with wrong password"
        login userId, "wrong password 3"

        then:
        errorMessage == 'Bad credentials'

        when: "Try login with correct password"
        login userId, password

        then:
        errorMessage == 'User account is locked'
    }

    def "Goto the change password page if the password is expired"() {
        given:
        String userId = "aUser"
        String password = "password"
        String newPassword = "newPasswd123<"

        userHelper.deleteUser(userId)
        userHelper.createUser(userId, password, "ADMIN") {
            passwordExpiresAt = Instant.now()
        }

        when:
        via TestPage

        then:
        at LoginPage

        when:
        login userId, password

        then:
        at ChangePasswordPage
        noErrorMessage
        fieldUserId == userId

        when:
        changePassword(password, newPassword, newPassword)

        then:
        at LoginPage

        when:
        login(userId, newPassword)

        then:
        at TestPage
    }
}
