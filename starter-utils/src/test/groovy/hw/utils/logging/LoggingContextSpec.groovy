package hw.utils.logging

import spock.lang.Specification

class LoggingContextSpec extends Specification {
    def "Get/set/remove MDC key"() {

        when:
        LoggingContext.setContextId()

        then:
        LoggingContext.getContextId()

        when:
        LoggingContext.removeContextId()

        then:
        !LoggingContext.getContextId()
    }
}
