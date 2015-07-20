package hw.oauth2.authentication.users

import hw.oauth2.Roles

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

    def "account enabled"() {
        when:
        UserDetails user = userDetailsBuilder().accountEnabled(true).build()

        then:
        user.enabled

        when:
        user = userDetailsBuilder().accountEnabled(false).build()

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
        user.authorities.authority as Set == [
            authority1,
            authority2,
            "ROLE_" + Roles.AUTHENTICATED] as Set
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
            "ROLE_" + role2,
            "ROLE_" + Roles.AUTHENTICATED] as Set
    }

    def "set forbidden role #role"(role) {

        when:
        UserDetails user = userDetailsBuilder().withRole(role).build()

        then:
        thrown IllegalStateException

        where:
        role << [
            Roles.MUST_CHANGE_PASSWORD,
            Roles.ACCOUNT_LOCKED
        ]
    }

    def "password expired"() {

        when:
        UserDetails user = userDetailsBuilder().passwordExpired(true).build()

        then:
        user.authorities.authority == [
            "ROLE_" + Roles.MUST_CHANGE_PASSWORD
        ]
    }

    def "password not expired"() {

        when:
        UserDetails user = userDetailsBuilder().passwordExpired(false).build()

        then:
        !user.authorities.authority.contains("ROLE_" + Roles.MUST_CHANGE_PASSWORD)
    }

    def "account locked"() {

        when:
        UserDetails user = userDetailsBuilder().accountLocked(true).build()

        then:
        user.authorities.authority == [
            "ROLE_" + Roles.ACCOUNT_LOCKED
        ]
    }

    def "account not locked"() {

        when:
        UserDetails user = userDetailsBuilder().accountLocked(false).build()

        then:
        !user.authorities.authority.contains("ROLE_" + Roles.ACCOUNT_LOCKED)
    }

    private UserDetailsBuilder userDetailsBuilder() {
        return new UserDetailsBuilder().userId("xx5656").password("hdfgfg")
    }
}
