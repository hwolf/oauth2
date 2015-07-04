package hw.web.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hw.web.logging.ExceptionLoggerAdvice;
import hw.web.logging.LoggingFilter;

@Configuration
public class LoggingConfiguration {

    @Bean
    public ExceptionLoggerAdvice exceptionLoggerAdvice() {
        return new ExceptionLoggerAdvice();
    }

    @Bean
    public FilterRegistrationBean loggingFilter() {
        LoggingFilter filter = createLoggingFilter();
        return createFilterRegistration(filter);
    }

    private FilterRegistrationBean createFilterRegistration(LoggingFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }

    private LoggingFilter createLoggingFilter() {
        return new LoggingFilter();
    }
}
