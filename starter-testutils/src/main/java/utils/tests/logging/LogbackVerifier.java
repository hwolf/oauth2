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
        Logger logger = getLogger();
        logger.addAppender(appender);
        logger.setLevel(level);
        logger.setAdditive(false);
    }

    @Override
    protected void after() {
        getLogger().detachAppender(appender);
    }

    protected Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(loggerName);
    }
}
