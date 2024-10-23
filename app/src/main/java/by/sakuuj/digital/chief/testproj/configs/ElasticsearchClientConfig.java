package by.sakuuj.digital.chief.testproj.configs;

import by.sakuuj.digital.chief.testproj.utils.Base64Utils;
import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration(proxyBeanMethods = false)
public class ElasticsearchClientConfig {


    @Bean
    public RestClient elasticsearchRestClient(@Value("${by.sakuuj.elasticsearch.server.url}")
                                              String serverUrl,
                                              @Value("${by.sakuuj.elasticsearch.password}")
                                              String password
    ) {

        String encodedCredentials = Base64Utils.encode("elastic:" + password);

        return RestClient.builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "Basic " + encodedCredentials)
                })
                .build();
    }



    @Bean
    public JacksonJsonpMapper jacksonJsonpMapper(ObjectMapper objectMapper) {

        return new JacksonJsonpMapper(objectMapper);
    }

    @Bean(destroyMethod = "close")
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient, JacksonJsonpMapper jacksonJsonpMapper) {

        return new RestClientTransport(restClient, jacksonJsonpMapper);
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport elasticsearchTransport) {

        return new ElasticsearchClient(elasticsearchTransport);
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(ElasticsearchTransport elasticsearchTransport) {

        return new ElasticsearchAsyncClient(elasticsearchTransport);
    }
}
