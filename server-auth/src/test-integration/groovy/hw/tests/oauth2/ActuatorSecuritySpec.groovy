package hw.tests.oauth2

import geb.spock.GebSpec
import hw.oauth.messages.user.CreateUserMessage
import hw.oauth.messages.user.DeleteUserMessage
import hw.oauth2.Application
import hw.oauth2.services.UserAdministrationService
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

    def setup() {
        browser.baseUrl = "http://localhost:${serverPort}/uaa/"
    }

    @Before
    void setupUser() {
        adminService.deleteUser(DeleteUserMessage.builder(USER_ID).build())
        adminService.createUser(CreateUserMessage.builder(USER_ID).password(PASSWORD).withRole(ROLE_ADMIN).build())
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
