package hw.web.logging

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import spock.lang.Specification

import hw.utils.logging.LoggingContext

class LoggingFilterSpec extends Specification {

    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    FilterChain chain = Mock()

    Filter filter = new LoggingFilter()

    def "ensure context id is set while invoking filter chain"() {

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * chain.doFilter(request, response) >> { assert !!LoggingContext.getContextId() }
    }

    def "ensure context id was removed when filter terminates"() {

        when:
        chain.doFilter(request, response) >> { /* do nothing */ }
        filter.doFilter(request, response, chain)

        then:
        !LoggingContext.getContextId()
    }

    def "ensure context id was removed event when filter throws an exception"() {

        when:
        chain.doFilter(request, response) >> { throw new ServletException() }
        filter.doFilter(request, response, chain)

        then:
        thrown(ServletException)
        !LoggingContext.getContextId()
    }
}
