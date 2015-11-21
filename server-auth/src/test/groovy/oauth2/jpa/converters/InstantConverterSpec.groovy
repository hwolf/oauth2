package oauth2.jpa.converters

import java.sql.Timestamp
import java.time.Instant

import spock.lang.Specification

class InstantConverterSpec extends Specification {

    def "Convert a timestamp from database to entity and back should return same timestamp"() {

        given:
        def timestamp = new Timestamp(3L)
        def converter = new InstantConverter()

        expect:
        timestamp == converter.convertToDatabaseColumn(converter.convertToEntityAttribute(timestamp))
    }

    def "Convert an instant from entity to database and back should return same instant"() {

        given:
        def instant = Instant.ofEpochMilli(3L)
        def converter = new InstantConverter()

        expect:
        instant == converter.convertToEntityAttribute(converter.convertToDatabaseColumn(instant))
    }

    def "Convert null from entity to database and back should return null"() {

        given:
        def converter = new InstantConverter()

        expect:
        null == converter.convertToEntityAttribute(converter.convertToDatabaseColumn(null))
    }
}
