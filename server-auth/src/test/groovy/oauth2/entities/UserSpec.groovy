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
package oauth2.entities

import java.time.Instant
import java.time.temporal.ChronoUnit

import spock.lang.Specification

class UserSpec extends Specification {

    def "when the password expires date is in the future, then the password is not expired"() {
        given:
        Instant passwordExpiresAt = Instant.now().plus(1, ChronoUnit.MINUTES)

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        !user.passwordExpired
    }

    def "when the password expires date is in the past, then the password is expired"() {
        given:
        Instant passwordExpiresAt = Instant.now()

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        user.passwordExpired
    }

    def "when no password expires date is given, then the password is expired"() {
        given:
        Instant passwordExpiresAt = null

        when:
        User user = new User(passwordExpiresAt: passwordExpiresAt)

        then:
        user.passwordExpired
    }

    def "when a password is given, then the user is enabled"() {
        given:
        String password = "given password"

        when:
        User user = new User(password: password)

        then:
        user.enabled
    }

    def "when no password is given, then the user is not enabled"() {
        given:
        String password = ""

        when:
        User user = new User(password: password)

        then:
        !user.enabled
    }

    def "with 2 failed login attempts, the account is not locked"() {
        given:
        int failedLogins = 2

        when:
        User user = new User()
        user.loginStatus.failedLoginAttempts = failedLogins

        then:
        !user.accountLocked
    }

    def "with 3 failed login attempts, the account is locked"() {
        given:
        int failedLogins = 3

        when:
        User user = new User()
        user.loginStatus.failedLoginAttempts = failedLogins

        then:
        user.accountLocked
    }

    def "getEntries: When no entries are added return empty list"() {

        when:
        User user = new User()
        def  entries = user.entries

        then:
        entries.empty
    }

    def "getEntries: Should return all entries"() {

        when:
        User user = new User()
        user.addEntry("Entry 1", "Data 1")
        user.addEntry("Entry 2", "Data 2")
        def entries = user.entries

        then:
        entries.name as Set == ["Entry 1", "Entry 2"] as Set
    }

    def "removeEntry: A removed entry should not in list"() {

        when:
        User user = new User()
        user.addEntry("Entry 1", "Data 1")
        user.addEntry("Entry 2", "Data 2")

        then:
        user.entries.name.contains("Entry 2")

        when:
        user.removeEntry("Entry 2", "Data 2")

        then:
        !user.entries.name.contains("Entry 2")
    }
}
