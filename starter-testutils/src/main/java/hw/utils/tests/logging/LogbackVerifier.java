package hw.utils.tests.logging;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A JUnit rule which captures log events.
 *
 * @see <a href="https://gist.github.com/tzachz/6048356">copied and modified for Log4J2</a>
 */
public class LogbackVerifier implements TestRule {

    private final Level level;
    private final String loggerName;

    private AppenderMock appender;

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
                base.evaluate();
            }
        };
    }

    public ILoggingEvent extractNextLogEvent() {
        return appender.extractNextEvent();
    }

    private void before() {
        appender = new AppenderMock();
        appender.start();
        addAppender();
    }

    private void addAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(loggerName);
        logger.addAppender(appender);
        logger.setLevel(level);
        logger.setAdditive(false);
    }
}
