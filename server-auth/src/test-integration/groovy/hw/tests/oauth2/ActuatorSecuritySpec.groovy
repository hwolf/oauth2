package hw.tests.oauth2

import geb.spock.GebSpec
import hw.oauth2.HwOauth2Application
import hw.tests.oauth2.pages.LoginPage

import org.junit.Before
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles

import spock.lang.Unroll

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = HwOauth2Application)
@ActiveProfiles([ "dev" ])
class ActuatorSecuritySpec extends GebSpec {

    static final USER_ID ="admin"
    static final PASSWORD = "admin"

    @Value('${local.server.port}')
    int serverPort

    @Before
    void setupUrl() {
        browser.baseUrl = "http://localhost:${serverPort}/uaa/"
    }

    @Unroll
    def "actuator endpoint #endpoint should protected"(endpoint) {
        when:
        go endpoint

        then:
        at(LoginPage)

        when:
        login USER_ID, PASSWORD

        then:
        currentUrl.endsWith endpoint
        !$('h1')

        where:
        endpoint << [
            'manage/autoconfig',
            'manage/beans',
            'manage/configprops',
            'manage/dump',
            'manage/env',
            'manage/health',
            'manage/info',
            'manage/metrics',
            'manage/mappings',
            'manage/trace',
            'manage/jolokia'
        ]
    }
}
