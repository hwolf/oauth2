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
package utils.logging

import org.junit.Rule
import org.slf4j.LoggerFactory

import spock.lang.Specification
import utils.tests.logging.LogbackVerifier
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent

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
        ILoggingEvent event = logVerifier.extractNextLogEvent()
        event.getMDCPropertyMap()[LoggingContext.MDC_KEY]
    }
}
