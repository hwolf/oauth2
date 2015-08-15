package hw.web.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration

import spock.lang.Specification

/**
 * Tests which check that the auto configuration in {@code /META-INF/spring.factories} is correct.
 */
@SpringApplicationConfiguration(classes = TestApplication)
class AutoConfigurationSpec extends Specification {

    @Autowired
    LoggingAutoConfiguration loggingConfig

    def "is logging auto configuration correctly configured in /META-INF/spring.factories"() {
        expect:
        loggingConfig != null
    }
}
