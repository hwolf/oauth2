package hw.utils.logging;

import java.util.UUID;

import org.slf4j.MDC;

import com.google.common.annotations.VisibleForTesting;

public class LoggingContext {

    @VisibleForTesting
    static final String MDC_KEY = "context";

    public static void setContextId() {
        MDC.put(MDC_KEY, UUID.randomUUID().toString());
    }

    public static void removeContextId() {
        MDC.remove(MDC_KEY);
    }

    public static String getContextId() {
        return MDC.get(MDC_KEY);
    }

    private LoggingContext() {
        // Avoid instantiation
    }
}
