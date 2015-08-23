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
package oauth2.authentication

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

import spock.lang.Specification

class MyAuthenticationSuccessHandlerSpec extends Specification {

    static final AUTHORITY_1 = "AUTHORITY_1"
    static final AUTHORITY_2 = "AUTHORITY_2"

    static final URL_1 = "/url1"
    static final URL_2 = "/url2"

    static final String DEFAULT_TARGET_URL = "/default-target-url"

    def "Use default behaviour if user is not authenticated"() {

        given:
        RedirectStrategy strategy = Mock()
        AuthenticationSuccessHandler handler = createMyAuthenticationSuccessHandler(strategy)

        HttpServletRequest request = Mock()
        HttpServletResponse response = Mock()
        Authentication authentication = new TestingAuthenticationToken("name", "password")

        when:
        authentication.setAuthenticated(false)
        handler.onAuthenticationSuccess(request, response, authentication)

        then:
        1 * strategy.sendRedirect(request, response, DEFAULT_TARGET_URL)
    }

    def "Use default behaviour if no authority is specified for a redirect"() {

        given:
        RedirectStrategy strategy = Mock()
        AuthenticationSuccessHandler handler = createMyAuthenticationSuccessHandler(strategy)

        HttpServletRequest request = Mock()
        HttpServletResponse response = Mock()
        Authentication authentication = new TestingAuthenticationToken("name", "password", "AN_AUTHORITY")

        when:
        authentication.setAuthenticated(true)
        handler.onAuthenticationSuccess(request, response, authentication)

        then:
        1 * strategy.sendRedirect(request, response, DEFAULT_TARGET_URL)
    }

    def "Redirect to a specific url if one authority is specified for a redirect"() {

        given:
        RedirectStrategy strategy = Mock()
        AuthenticationSuccessHandler handler = createMyAuthenticationSuccessHandler(strategy)

        HttpServletRequest request = Mock()
        HttpServletResponse response = Mock()
        Authentication authentication = new TestingAuthenticationToken("name", "password", AUTHORITY_1)

        when:
        authentication.setAuthenticated(true)
        handler.onAuthenticationSuccess(request, response, authentication)

        then:
        1 * strategy.sendRedirect(request, response, URL_1)
    }

    private AuthenticationSuccessHandler createMyAuthenticationSuccessHandler(RedirectStrategy strategy) {
        MyAuthenticationSuccessHandler handler = new MyAuthenticationSuccessHandler()
        handler.addRedirect(AUTHORITY_1, URL_1)
        handler.addRedirect(AUTHORITY_2, URL_2)
        handler.alwaysUseDefaultTargetUrl = true
        handler.defaultTargetUrl = DEFAULT_TARGET_URL
        handler.redirectStrategy = strategy
        return handler
    }
}
