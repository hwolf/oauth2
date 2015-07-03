package hw.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Configuration
public class UtilsConfiguration {

    @Bean
    public JSR310Module jsr310Module() {
        return new JSR310Module();
    }

    @Bean
    public GuavaModule guavaModule() {
        return new GuavaModule();
    }
}
