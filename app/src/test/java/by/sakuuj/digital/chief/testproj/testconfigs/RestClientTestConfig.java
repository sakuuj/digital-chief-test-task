package by.sakuuj.digital.chief.testproj.testconfigs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class RestClientTestConfig {

    @Bean
    public RestClient restClient() {

        return RestClient.builder().build();
    }
}
