package hw.oauth2.authentication

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

import spock.lang.Specification

class MyPasswordEncoderSpec extends Specification {

    private static final String NAME_ENCODER = 'default'

    def "Password encoding should be done with the default password encoder"() {

        given:
        PasswordEncoder encoder = createDefaultEncoder().build()

        when:
        String encodedPassword = encoder.encode('password')

        then:
        encodedPassword.startsWith(NAME_ENCODER + MyPasswordEncoder.SEPARATOR)
    }

    def "Password matching should be done with the default password encoder"() {

        given:
        PasswordEncoder encoder = createDefaultEncoder().build()

        when:
        String encodedPassword = encoder.encode('My Password')

        then:
        encoder.matches('My Password', encodedPassword)
    }

    def "If encoded password doesn't contain a encoder name, use default encoder"() {

        given:
        PasswordEncoder encoder = createDefaultEncoder().build()

        expect:
        encoder.matches('password', 'password')
    }

    def "A password encoded with an old password encoder, should match using the old password encoder"() {

        given:
        PasswordEncoder oldEncoder = new ReversePasswordEncoder()
        PasswordEncoder encoder = createDefaultEncoder().withEncoder('old', oldEncoder).build()

        when:
        String encodedPassword = 'old' + MyPasswordEncoder.SEPARATOR + 'My Password'.reverse()

        then:
        encoder.matches('My Password', encodedPassword)
    }

    private static MyPasswordEncoder.Builder createDefaultEncoder() {
        return MyPasswordEncoder.builder() //
                .defaultEncoder(NAME_ENCODER, new BCryptPasswordEncoder(4))
    }
}
