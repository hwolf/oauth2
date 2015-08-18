package oauth2.tests

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import groovy.transform.CompileStatic

@CompileStatic
@Configuration
class TestpageConfiguration extends WebMvcConfigurerAdapter {

    @Override
    void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api/test").setViewName("testpage")
    }
}