package hw.tests.oauth2

import geb.spock.GebSpec
import hw.oauth2.Application
import hw.oauth2.services.admin.UserAdministrationService
import hw.tests.oauth2.pages.LoginPage

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles

import spock.lang.Unroll

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = Application)
@ActiveProfiles([ "dev" ])
class ActuatorSecuritySpec extends GebSpec {

    static final USER_ID ="admin1"
    static final PASSWORD = "password1"
    static final ROLE_ADMIN = "ADMIN"

    @Value('${local.server.port}')
    int serverPort

    @Autowired
    UserAdministrationService adminService

    @Before
    void setupUrl() {
        browser.baseUrl = "http://localhost:${serverPort}/uaa/"
    }

    @Before
    void setupUser() {
        UserHelper helper = new UserHelper(adminService)
        helper.deleteUser(USER_ID)
        helper.createUser(USER_ID, PASSWORD, ROLE_ADMIN)
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
