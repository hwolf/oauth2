package hw.oauth.messages.user

import spock.lang.Specification

class DeleteUserMessageSpec extends Specification {

    def "set user id"() {
        given:
        String userId = "x747474"

        when:
        DeleteUserMessage msg = DeleteUserMessage.builder(userId).build()

        then:
        msg.userId == userId
    }
}
