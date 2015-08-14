package hw.utils.tests.logging;

import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * A JUnit rule which captures log events.
 */
public class LogbackVerifier implements TestRule {

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

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } finally {
                    after();
                }
            }
        };
    }

    public List<ILoggingEvent> getEvents() {
        return appender.list;
    }

    public ILoggingEvent extractNextLogEvent() {
        return appender.list.remove(0);
    }

    private void before() {
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

    private void after() {
        getLogger().detachAppender(appender);
    }

    protected Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(loggerName);
    }
}
