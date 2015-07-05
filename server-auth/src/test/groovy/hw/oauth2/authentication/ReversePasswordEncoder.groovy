package hw.oauth2.authentication

import org.springframework.security.crypto.password.PasswordEncoder

class ReversePasswordEncoder implements PasswordEncoder {

    String encode(CharSequence rawPassword) {
        return rawPassword.toString().reverse()
    }

    boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword) == encodedPassword
    }
}
