package org.chusete.reactorpatterns;

import org.chusete.reactorpatterns.configuration.PropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration.class)
public class ReactorPatternsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReactorPatternsApplication.class, args);
    }
}
