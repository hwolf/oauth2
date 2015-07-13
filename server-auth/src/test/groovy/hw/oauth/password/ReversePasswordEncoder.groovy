package hw.oauth.password

import org.springframework.security.crypto.password.PasswordEncoder

/**
 * A simple password encoder which encodes a password by reversing the password.
 *
 * This is just for testing purpose to distinguish a raw password and an encoded password.
 */
class ReversePasswordEncoder implements PasswordEncoder {

    String encode(CharSequence rawPassword) {
        return rawPassword.toString().reverse()
    }

    boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword) == encodedPassword
    }
}
