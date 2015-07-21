package hw.oauth2.authentication

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
