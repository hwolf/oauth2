package hw.oauth.messages.user

import hw.oauth.messages.UpdateAction
import spock.lang.Specification

class UpdateUserMessageSpec extends Specification {

    static final String USER_ID = "x123456"

    def "set user id"() {
        given:
        String userId = "d3434"

        when:
        UpdateUserMessage msg = UpdateUserMessage.builder(userId).build()

        then:
        msg.userId == userId
    }

    def "add/remove authorities"() {
        given:
        String authority1 = "authority to add"
        String authority2 = "another authority to add"
        String authority3 = "authority to remove"
        UpdateUserMessage.Visitor visitor = Mock()

        when:
        UpdateUserMessage msg = UpdateUserMessage.builder("fhfgfg") //
                .addAuthority(authority1).addAuthority(authority2).removeAuthority(authority3).build()

        msg.visitChanges(visitor)

        then:
        1 * visitor.visitAuthority(UpdateAction.ADD, authority1)
        1 * visitor.visitAuthority(UpdateAction.ADD, authority2)
        1 * visitor.visitAuthority(UpdateAction.REMOVE, authority3)
        0 * _
    }

    def "add/remove roles"() {
        given:
        String role1 = "role to add"
        String role2 = "role to remove"
        String role3 = "another role to remove"
        UpdateUserMessage.Visitor visitor = Mock()

        when:
        UpdateUserMessage msg = UpdateUserMessage.builder("fhfgfg") //
                .addRole(role1).removeRole(role2).removeRole(role3).build()

        msg.visitChanges(visitor)

        then:
        1 * visitor.visitAuthority(UpdateAction.ADD, "ROLE_" + role1)
        1 * visitor.visitAuthority(UpdateAction.REMOVE, "ROLE_" + role2)
        1 * visitor.visitAuthority(UpdateAction.REMOVE, "ROLE_" + role3)
        0 * _
    }
}
