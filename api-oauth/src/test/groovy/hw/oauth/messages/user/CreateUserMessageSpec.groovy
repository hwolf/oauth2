package hw.oauth.messages.user

import spock.lang.Specification

class CreateUserMessageSpec extends Specification {

    static final ROLE_PREFIX = "ROLE_"

    def "set user id and password"() {
        given:
        String userId = "xaaaas"
        String password = "dfhghfhf"

        when:
        CreateUserMessage msg = CreateUserMessage.builder(userId).password(password).build()

        then:
        msg.userId == userId
        msg.password == password
    }

    def "set authorities"() {
        given:
        String authority1 = "auth 1"
        String authority2 = "auth 2"
        String authority3 = "auth 3"

        when:
        CreateUserMessage msg = CreateUserMessage.builder("f4433") //
                .withAuthorities(authority1, authority2) //
                .withAuthority(authority3) //
                .build()

        then:
        msg.authorities == [
            authority1,
            authority2,
            authority3] as Set
    }

    def "set roles"() {
        given:
        String role1 = "role 1"
        String role2 = "role 2"
        String role3 = "role 3"

        when:
        CreateUserMessage msg = CreateUserMessage.builder("g5555") //
                .withRoles(role1, role2) //
                .withRole(role3) //
                .build()

        then:
        msg.authorities == [
            ROLE_PREFIX + role1,
            ROLE_PREFIX + role2,
            ROLE_PREFIX + role3] as Set
    }
}
