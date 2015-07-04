package hw.web.logging

import hw.utils.logging.LoggingContext

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import spock.lang.Specification

class LoggingFilterSpec extends Specification {

    def "ensure context id is set while invoking filter chain"() {

        given:
        HttpServletRequest request = Mock()
        HttpServletResponse response = Mock()
        FilterChain chain = Mock()

        when:
        Filter filter = new LoggingFilter()
        filter.doFilter(request, response, chain)

        then:
        !LoggingContext.getContextId()
        1 * chain.doFilter(request, response) >> { assert !!LoggingContext.getContextId() }
    }
}
