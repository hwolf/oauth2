package hw.utils.logging

import org.junit.Rule
import org.slf4j.LoggerFactory

import spock.lang.Specification
import ch.qos.logback.classic.Level

import hw.utils.tests.logging.LogbackVerifier

class LoggingContextLoggingEventContainsMdcKeySpec extends Specification {

    final String LOGGER_NAME = "logger name"

    @Rule
    final LogbackVerifier logVerifier = new LogbackVerifier(Level.INFO, LOGGER_NAME)

    def "Ensure ILoggingEvent contains MDC key"() {

        given:
        String logMessage = "A log message on level info"

        when:
        LoggingContext.setContextId()
        LoggerFactory.getLogger(LOGGER_NAME).info(logMessage)

        then:
        logVerifier.events.size == 1
        logVerifier.events[0].getMDCPropertyMap()[LoggingContext.MDC_KEY]
    }
}
