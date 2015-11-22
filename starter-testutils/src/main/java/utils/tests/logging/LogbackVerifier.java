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
package utils.tests.logging;

import org.junit.rules.ExternalResource;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * A JUnit rule which captures log events.
 */
public class LogbackVerifier extends ExternalResource {

    private final Level level;
    private final String loggerName;

    private ListAppender<ILoggingEvent> appender;

    public LogbackVerifier(Level info, Class<?> loggerName) {
        this(info, loggerName.getName());
    }

    public LogbackVerifier(Level level, String loggerName) {
        this.level = level;
        this.loggerName = loggerName;
    }

    public ILoggingEvent extractNextLogEvent() {
        return appender.list.remove(0);
    }

    @Override
    protected void before() {
        appender = new ListAppender<>();
        addAppender();
        appender.start();
    }

    private void addAppender() {
        getLogger().addAppender(appender);
        getLogger().setLevel(level);
        getLogger().setAdditive(false);
    }

    @Override
    protected void after() {
        getLogger().detachAppender(appender);
    }

    protected Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(loggerName);
    }
}
