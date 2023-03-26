package org.chusete.reactorpatterns.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "my-app-params")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class PropertiesConfiguration {
    private Integer maxNumWords;
    private Integer maxWordLength;
}
