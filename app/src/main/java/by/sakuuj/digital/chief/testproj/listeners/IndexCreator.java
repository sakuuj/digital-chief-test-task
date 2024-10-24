package by.sakuuj.digital.chief.testproj.listeners;

import by.sakuuj.digital.chief.testproj.exception.IndexAlreadyExistsException;
import by.sakuuj.digital.chief.testproj.service.IndexCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IndexCreator implements ApplicationListener<ContextRefreshedEvent> {

    private final IndexCreationService indexCreationService;
    private final String indexName;
    private static final String JSON_FILE_NAME = "main-index.json";

    public IndexCreator(
            IndexCreationService indexCreationService,
            @Value("${by.sakuuj.elasticsearch.main-index.name}") String indexName
    ) {
        this.indexCreationService = indexCreationService;
        this.indexName = indexName;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            indexCreationService.createIndex(indexName, JSON_FILE_NAME);

        } catch (IndexAlreadyExistsException ex) {
            log.info(ex.getMessage());
        }
    }
}
