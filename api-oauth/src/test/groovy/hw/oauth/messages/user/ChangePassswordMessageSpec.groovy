package hw.oauth.messages.user

import spock.lang.Specification

class ChangePassswordMessageSpec extends Specification {

    def "set user id"() {
        given:
        String userId = "x747474"

        when:
        ChangePasswordMessage msg = ChangePasswordMessage.builder(userId).build()

        then:
        msg.userId == userId
    }

    def "set old password"() {
        given:
        String oldPassword = "Old password"

        when:
        ChangePasswordMessage msg = ChangePasswordMessage.builder("x4747").oldPassword(oldPassword).build()

        then:
        msg.oldPassword == oldPassword
    }

    def "set new password"() {
        given:
        String newPassword = "New password"

        when:
        ChangePasswordMessage msg = ChangePasswordMessage.builder("x4747").newPassword(newPassword).build()

        then:
        msg.newPassword == newPassword
    }
}
