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
