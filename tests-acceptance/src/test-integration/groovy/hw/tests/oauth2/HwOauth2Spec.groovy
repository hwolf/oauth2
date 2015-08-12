package hw.tests.oauth2

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles

import geb.spock.GebReportingSpec

import hw.oauth2.HwOauth2Application
import hw.oauth2.entities.UserRepository
import hw.tests.oauth2.utils.UserHelper

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = [HwOauth2Application, TestpageConfiguration])
@ActiveProfiles([ "acceptance-tests" ])
abstract class HwOauth2Spec extends GebReportingSpec {

    @Value('${local.server.port}')
    int serverPort

    UserHelper userHelper

    @Autowired
    void setUserRepository(UserRepository userRepository) {
        userHelper = new UserHelper(userRepository)
    }

    @Before
    void setupUrl() {
        browser.baseUrl = "http://localhost:${serverPort}/uaa/"
    }
}
