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

class ClientSpec extends Specification {

    def "getEntries: When no entries are added return empty list"() {

        when:
        Client client = new Client()
        def  entries = client.entries

        then:
        entries.empty
    }

    def "getEntries: Should return all entries"() {

        when:
        Client client = new Client()
        client.addEntry("Entry 1", "Data 1")
        client.addEntry("Entry 2", "Data 2")
        def entries = client.entries

        then:
        entries.name as Set == ["Entry 1", "Entry 2"] as Set
    }

    def "removeEntry: A removed entry should not in list"() {

        when:
        Client client = new Client()
        client.addEntry("Entry 1", "Data 1")
        client.addEntry("Entry 2", "Data 2")

        then:
        client.entries.name.contains("Entry 2")

        when:
        client.removeEntry("Entry 2", "Data 2")

        then:
        !client.entries.name.contains("Entry 2")
    }
}
