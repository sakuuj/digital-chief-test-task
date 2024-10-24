package by.sakuuj.digital.chief.testproj.service.impl;

import by.sakuuj.digital.chief.testproj.exception.IndexAlreadyExistsException;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import by.sakuuj.digital.chief.testproj.utils.JsonExtractorUtils;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

@Service
public class IndexCreationServiceImpl implements IndexCreationService {

    private final String indexJsonFilesBasePath;
    private final ElasticsearchClient esClient;

    public IndexCreationServiceImpl(
            @Value("${by.sakuuj.elasticsearch.json-files.index.base-path}") String indexJsonFilesBasePath,
            ElasticsearchClient esClient
    ) {
        this.esClient = esClient;
        this.indexJsonFilesBasePath = indexJsonFilesBasePath;
    }

    public void createIndex(String indexName, String jsonFileName) {

        String jsonContent = JsonExtractorUtils.fromFile(indexJsonFilesBasePath + jsonFileName);
        Reader input = new StringReader(jsonContent);

        try {
            BooleanResponse existsResponse = esClient.indices().exists(b -> b.index(indexName));
            if (existsResponse.value()) {

                throw new IndexAlreadyExistsException("Index '%s' already exists".formatted(indexName));
            }

            esClient.indices().create(b -> b.index(indexName).withJson(input));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
