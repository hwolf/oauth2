package hw.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = { GroovyTemplateAutoConfiguration.class })
@PropertySource("classpath:starter-web.properties")
@PropertySource("classpath:build-info.properties")
public class ApplicationBase {

    public static SpringApplicationBuilder applicationBuilder(Class<?> source) {
        return new SpringApplicationBuilder(source).banner(new GeneratedBanner());
    }

    public static void runApplication(Class<?> source, String[] args) {
        applicationBuilder(source).run(args);
    }
}
