package oauth2.tests.acceptance

import geb.spock.GebReportingSpec

class AuthorizeSpec extends GebReportingSpec {

    def "successful authorization"() {

        when:
        to HomePage

        then:
        at HomePage

        when:
        login.click()

        then:
        at LoginPage

        when:
        login "user1", "user1"

        then:
        at AuthorizePage

        when:
        authorize(['scope1', 'scope2'])

        then:
        at HomePage
    }
}
