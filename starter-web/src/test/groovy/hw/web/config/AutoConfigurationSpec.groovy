package hw.web.config

import hw.web.config.testapp.TestApplication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration

import spock.lang.Specification

/**
 * Tests which checks that the auto configuration in {@code /META-INF/spring.factories} is correct.
 * <p>
 * Hint: The class {@code TestApplication} must be in sub package, otherwise the classes {@code hw.web.config} will be found by component scan.
 * </p>
 */
@SpringApplicationConfiguration(classes = TestApplication)
class AutoConfigurationSpec extends Specification {

    @Autowired
    LoggingAutoConfiguration loggingConfig

    @Autowired
    ErrorPagesAutoConfiguration errorPageConfig

    def "is logging auto configuration correctly configured in /META-INF/spring.factories"() {
        expect:
        loggingConfig != null
    }

    def "is error page auto configuration correctly configured in /META-INF/spring.factories"() {
        expect:
        errorPageConfig != null
    }
}
