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
