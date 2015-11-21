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

class ApprovalPkSpec extends Specification {

    def "Default constructor: All properties should be null"() {

        when:
        def pk = new ApprovalPK()

        then:
        pk.userId == null
        pk.clientId == null
        pk.scope == null
    }

    def "Constructor: All properties should have a given value"() {

        given:
        def userId = "A user id"
        def clientId = "A client id"
        def scope = "A scope"

        when:
        def pk = new ApprovalPK(userId, clientId, scope)

        then:
        pk.userId == userId
        pk.clientId == clientId
        pk.scope == scope
    }
}
