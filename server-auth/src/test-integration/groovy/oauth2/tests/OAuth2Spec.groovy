package oauth2.tests

import oauth2.OAuth2Application
import oauth2.entities.UserRepository
import oauth2.tests.utils.UserHelper

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles

import geb.spock.GebReportingSpec

@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = [OAuth2Application, TestpageConfiguration])
@ActiveProfiles([ "dev" ])
abstract class OAuth2Spec extends GebReportingSpec {

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
