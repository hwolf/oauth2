package hw.tests.oauth2

import geb.spock.GebSpec
import hw.oauth2.Application
import hw.tests.oauth2.pages.LoginPage

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

import spock.lang.Unroll

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application)
@ActiveProfiles([ "dev" ])
@Sql(scripts = "users.sql")
class ActuatorSecuritySpec extends GebSpec {

    @Value('${local.server.port}')
    int serverPort

    def setup() {
        browser.baseUrl = "http://localhost:${serverPort}/uaa/"
    }

    @Unroll
    def "actuator endpoint #endpoint should protected"(endpoint) {
        when:
        go endpoint

        then:
        at(LoginPage)

        when:
        login "admin1", "admin1"

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

