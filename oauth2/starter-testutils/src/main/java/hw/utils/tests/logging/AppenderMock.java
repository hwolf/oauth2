package hw.utils.tests.logging;

import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.google.common.collect.Lists;

/**
 * A Logback appender which appends all log events to a list.
 */
public class AppenderMock extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = Lists.newLinkedList();

    public ILoggingEvent extractNextEvent() {
        return events.remove(0);
    }

    @Override
    public void append(ILoggingEvent event) {
        events.add(event);
    }
}
