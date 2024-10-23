package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.configs.ElasticsearchClientConfig;
import by.sakuuj.digital.chief.testproj.configs.JacksonConfig;
import by.sakuuj.digital.chief.testproj.testconfigs.RestClientTestConfig;
import by.sakuuj.digital.chief.testproj.testcontainers.ElasticsearchSingletonContainerLauncher;
import by.sakuuj.digital.chief.testproj.utils.JsonExtractorUtils;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        JacksonConfig.class,
        ElasticsearchClientConfig.class,
        IndexCreationServiceImpl.class
})
@SpringJUnitConfig(classes = RestClientTestConfig.class)
class IndexCreationServiceImplTest extends ElasticsearchSingletonContainerLauncher {

    @Autowired
    private IndexCreationServiceImpl serviceImpl;

    @Autowired
    private ElasticsearchClient esClient;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INDEX_FILE_NAME = "main-index.json";
    private static final String INDEX_BASE_PATH = "elasticsearch/index/";
    private static final String INDEX_NAME = "my-index";

    @DynamicPropertySource
    static void setAdditionalDynamicProps(DynamicPropertyRegistry registry) {
        registry.add("by.sakuuj.elasticsearch.json-files.index.base-path", () -> INDEX_BASE_PATH);
    }

    @Test
    void shouldCreateIndexFromJson() throws IOException, JSONException {

        // given
        serviceImpl.createIndex(INDEX_NAME, INDEX_FILE_NAME);

        // when, then
        BooleanResponse existsResponse = esClient.indices().exists(b -> b.index(INDEX_NAME));
        assertThat(existsResponse.value()).isTrue();

        ResponseEntity<String> mappingsResponse = restClient.get()
                .uri(getContainerUrl() + "/" + INDEX_NAME + "/_mappings")
                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeaderValue())
                .retrieve()
                .toEntity(String.class);
        assertThat(mappingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode actual = objectMapper.readTree(mappingsResponse.getBody()).get(INDEX_NAME);
        String expectedJson = JsonExtractorUtils.fromFile(INDEX_BASE_PATH + INDEX_FILE_NAME);
        JSONAssert.assertEquals(expectedJson, actual.toPrettyString(), false);

        esClient.indices().delete(b -> b.index(INDEX_NAME));
    }
}
