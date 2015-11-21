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
