package hw.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = { GroovyTemplateAutoConfiguration.class })
public class ApplicationBase {

    public static SpringApplicationBuilder applicationBuilder(Class<?> source) {
        return new SpringApplicationBuilder(source).banner(new GeneratedBanner());
    }

    public static void runApplication(Class<?> source, String[] args) {
        applicationBuilder(source).run(args);
    }
}
