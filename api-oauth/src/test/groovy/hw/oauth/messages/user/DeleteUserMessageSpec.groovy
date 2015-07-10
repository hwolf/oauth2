package hw.oauth.messages.user

import spock.lang.Specification

class DeleteUserMessageSpec extends Specification {

    static final String USER_ID = "x123456"

    def "set user id"() {
        when:
        DeleteUserMessage msg = DeleteUserMessage.builder(USER_ID).build()

        then:
        msg.userId == USER_ID
    }
}
