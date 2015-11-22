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
package web.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import web.logging.ExceptionLoggerAdvice;
import web.logging.LoggingFilter;

public class LoggingAutoConfiguration {

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
