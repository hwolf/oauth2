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
