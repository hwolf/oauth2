/*
 * Copyright 2015 H. Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oauth2.password

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
