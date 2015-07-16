package hw.oauth2.authentication.users

import org.springframework.security.core.userdetails.UserDetails

import spock.lang.Specification

class UserDetailsBuilderSpec extends Specification {

    def "set user id"() {
        given:
        String userId = "a user id"

        when:
        UserDetails user = userDetailsBuilder().userId(userId).build()

        then:
        user.username == userId
    }

    def "set password"() {
        given:
        String password = "a password"

        when:
        UserDetails user = userDetailsBuilder().password(password).build()

        then:
        user.password == password
    }

    def "when no password is given, then the user is not enabled"() {
        given:
        String password = ""

        when:
        UserDetails user = userDetailsBuilder().password(password).build()

        then:
        !user.enabled
    }
    def "set authorities"() {
        given:
        String authority1 = "authority 1"
        String authority2 = "authority 2"

        when:
        UserDetails user = userDetailsBuilder().withAuthority(authority1).withAuthority(authority2).build()

        then:
        user.authorities.authority as Set == [authority1, authority2] as Set
    }

    def "set roles"() {
        given:
        String role1 = "role 1"
        String role2 = "role 2"

        when:
        UserDetails user = userDetailsBuilder().withRole(role1).withRole(role2).build()

        then:
        user.authorities.authority as Set == [
            "ROLE_" + role1,
            "ROLE_" + role2] as Set
    }

    private UserDetailsBuilder userDetailsBuilder() {
        return new UserDetailsBuilder().userId("xx5656").password("hdfgfg")
    }
}
