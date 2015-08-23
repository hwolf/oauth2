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
