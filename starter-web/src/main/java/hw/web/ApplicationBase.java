package hw.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;

import hw.utils.config.UtilsConfiguration;
import hw.web.config.LoggingConfiguration;

@SpringBootApplication(exclude = { GroovyTemplateAutoConfiguration.class })
@Import({ LoggingConfiguration.class, UtilsConfiguration.class })
@PropertySource("classpath:starter-web.properties")
@PropertySource("classpath:build-info.properties")
public class ApplicationBase {

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

    public static SpringApplicationBuilder applicationBuilder(Class<?> source) {
        return new SpringApplicationBuilder(source).banner(new GeneratedBanner());
    }

    public static void runApplication(Class<?> source, String[] args) {
        applicationBuilder(source).run(args);
    }
}
