package hw.web.logging

import org.junit.Rule
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import spock.lang.Specification
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent

import hw.utils.tests.logging.LogbackVerifier

/**
 * Tests for {@link ExceptionLoggerAdvice}.
 */
class ExceptionLoggerAdviceSpec extends Specification {

    static class MyException extends Exception {
    }

    @Rule
    public final LogbackVerifier logVerifier = new LogbackVerifier(Level.ERROR, ExceptionLoggerAdvice.class.name)

    def "an exception should be logged"() {

        given:
        def request = new MockHttpServletRequest(requestURI: "/my-path", queryString: "x=x&a=b")
        def response = new MockHttpServletResponse()

        when:
        new ExceptionLoggerAdvice().logException(request, response, new MyException())

        then:
        ILoggingEvent event = logVerifier.extractNextLogEvent()
        event.level == Level.ERROR
        event.throwableProxy.className == MyException.class.name
        event.formattedMessage.contains("/my-path?x=x&a=b")
    }
}
