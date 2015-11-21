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

import spock.lang.Specification

class EntrySpec extends Specification {

    def "create: Getters should return values from factory method"() {

        given:
        String expectedName = "A entry name"
        String expectedData = "A entry value"

        when:
        Entry entry = Entry.create(expectedName, expectedData)

        then:
        expectedName == entry.name
        expectedData == entry.data
    }

    def "filterEntriesByName: When parameter entries is null return empty list"() {

        when:
        def  actual = Entry.filterEntriesByName("A entry name", null)

        then:
        actual == [] as Set
    }

    def "filterEntriesByName: Should return data from all entries with given name"() {

        when:
        def  actual = Entry.filterEntriesByName("Entry 1", [
            Entry.create("Entry 1", "Data entry 1"),
            Entry.create("Entry 2", "Data entry 2")
        ])

        then:
        actual == ["Data entry 1"] as Set
    }
}
