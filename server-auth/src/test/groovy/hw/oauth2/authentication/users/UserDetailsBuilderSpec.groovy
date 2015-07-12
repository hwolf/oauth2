package hw.oauth2.authentication.users

import java.time.Instant
import java.time.temporal.ChronoUnit

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

    def "when the password expires date is in the future, then the credentials are not expired"() {
        given:
        Instant passwordExpiresAt = Instant.now().plus(1, ChronoUnit.MINUTES)

        when:
        UserDetails user = userDetailsBuilder().passwordExpiresAt(passwordExpiresAt).build()

        then:
        user.credentialsNonExpired
    }

    def "when the password expires date is in the past, then the credentials are expired"() {
        given:
        Instant passwordExpiresAt = Instant.now()

        when:
        UserDetails user = userDetailsBuilder().passwordExpiresAt(passwordExpiresAt).build()

        then:
        !user.credentialsNonExpired
    }

    def "when no password expires date is given, then the credentials are expired"() {
        given:
        Instant passwordExpiresAt = null

        when:
        UserDetails user = userDetailsBuilder().passwordExpiresAt(passwordExpiresAt).build()

        then:
        !user.credentialsNonExpired
    }

    def "when a passoword is given, then the user is enabled"() {
        given:
        String password = "given password"

        when:
        UserDetails user = userDetailsBuilder().password(password).build()

        then:
        user.enabled
    }

    def "when no password is given, then the user is not enabled"() {
        given:
        String password = ""

        when:
        UserDetails user = userDetailsBuilder().password(password).build()

        then:
        !user.enabled
    }

    def "with 3 failed login attempts, the account is not locked"() {
        given:
        int failedLogins = 3

        when:
        UserDetails user = userDetailsBuilder().failedLogins(failedLogins).build()

        then:
        user.accountNonLocked
    }

    def "with 4 failed login attempts, the account is locked"() {
        given:
        int failedLogins = 4

        when:
        UserDetails user = userDetailsBuilder().failedLogins(failedLogins).build()

        then:
        !user.accountNonLocked
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
