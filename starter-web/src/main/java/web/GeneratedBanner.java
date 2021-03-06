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
package web;

import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import static org.springframework.boot.ansi.AnsiElement.DEFAULT;
import static org.springframework.boot.ansi.AnsiElement.FAINT;
import static org.springframework.boot.ansi.AnsiElement.GREEN;

import com.github.lalyos.jfiglet.FigletFont;
import com.google.common.base.Joiner;

/**
 * Generates a Spring Boot banner by using
 * <a href="https://github.com/lalyos/jfiglet">https://github.com/lalyos/jfiglet</a>. The banner
 * shows also the Spring version, Spring Boot version, and the active profiles.
 */
public class GeneratedBanner implements Banner {

    private static final String SPRING = " :: Spring          :: ";
    private static final String SPRING_BOOT = " :: Spring Boot     :: ";
    private static final String ACTIVE_PROFILES = " :: Active Profiles :: ";

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

        printApplicationName(out, environment, sourceClass);

        printInfoText(out, SPRING, getSpringVersion());
        printInfoText(out, SPRING_BOOT, getSpringBootVersion());
        printInfoText(out, ACTIVE_PROFILES, getActiveProfiles(environment));
        out.println();
    }

    private void printApplicationName(PrintStream out, Environment environment, Class<?> sourceClass) {
        String banner = generateBanner(environment, sourceClass);
        out.println(banner);
    }

    private String generateBanner(Environment environment, Class<?> sourceClass) {
        String key = environment.getProperty("spring.application.name", sourceClass.getSimpleName());
        try {
            return FigletFont.convertOneLine(key);
        } catch (IOException ex) {
            LoggerFactory.getLogger(getClass()).warn("Cannot generated banner", ex);
            return key;
        }
    }

    private void printInfoText(PrintStream out, String key, String message) {
        if (StringUtils.hasText(message)) {
            out.println(AnsiOutput.toString(GREEN, key, DEFAULT, FAINT, message));
        }
    }

    private String getSpringVersion() {
        return SpringVersion.getVersion();
    }

    private String getSpringBootVersion() {
        // With Spring Boot 1.3 change to:
        // return SpringBootVersion.getVersion()
        return Banner.class.getPackage().getImplementationVersion();
    }

    private String getActiveProfiles(Environment environment) {
        return Joiner.on(", ").join(environment.getActiveProfiles());
    }
}
