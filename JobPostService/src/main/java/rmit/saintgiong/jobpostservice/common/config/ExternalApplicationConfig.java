package rmit.saintgiong.jobpostservice.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external.application")
@Data
public class ExternalApplicationConfig {
    private String serviceUrl = "https://sgja-api.vohoangphuc.com";
}
