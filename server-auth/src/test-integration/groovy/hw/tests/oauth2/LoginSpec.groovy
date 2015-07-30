package hw.tests.oauth2

import hw.tests.oauth2.pages.ChangePasswordPage
import hw.tests.oauth2.pages.LoginPage
import hw.tests.oauth2.pages.TestPage
import hw.tests.oauth2.utils.UserHelper

import java.time.Instant

class LoginSpec extends HwOauth2Spec {

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

        when:
        login userId, password + "1"

        then:
        errorMessage == 'Bad credentials'

        when:
        login userId, password + "2"

        then:
        errorMessage == 'Bad credentials'

        when:
        login userId, password + "3"

        then:
        errorMessage == 'Bad credentials'

        when: // Even
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
