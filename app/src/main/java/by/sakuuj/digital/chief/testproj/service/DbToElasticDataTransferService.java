package by.sakuuj.digital.chief.testproj.service;

import java.time.LocalDateTime;

public interface DbToElasticDataTransferService {

    void createIndex(String indexName, String jsonFileName);

    void transferProductsCreatedAtAfterPointInTime(LocalDateTime pointInTime, int perPageSize);

    void transferSkusCreatedAtAfterPointInTime(LocalDateTime pointInTime, int perPageSize);
}
