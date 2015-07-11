package hw.web.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorPagesAutoConfiguration {

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new ErrorPagesCustomizer();
    }

    private static class ErrorPagesCustomizer implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error-pages/401.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error-pages/403.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error-pages/404.html"));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-pages/500.html"));
        }
    }
}
