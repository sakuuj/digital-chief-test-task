package by.sakuuj.digital.chief.testproj.controller;


import by.sakuuj.digital.chief.testproj.service.DbToElasticDataTransferService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/data-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataTransferController {

    private final DbToElasticDataTransferService dbToElasticDataTransferService;

    @PostMapping("/products")
    public ResponseEntity<Void> transferProducts(
            @Parameter(example = "2022-01-01T00:00:00") @RequestParam(value = "after") LocalDateTime afterDateTime,
            @RequestParam(value = "perPageSize", defaultValue = "9") @Positive int perPageSize
    ) {
        dbToElasticDataTransferService.transferProductsCreatedAtAfterPointInTime(afterDateTime, perPageSize);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/skus")
    public ResponseEntity<Void> transferSkus(
            @Parameter(example = "2022-01-01T00:00:00") @RequestParam(value = "after") LocalDateTime afterDateTime,
            @RequestParam(value = "perPageSize", defaultValue = "9") @Positive int perPageSize
    ) {
        dbToElasticDataTransferService.transferSkusCreatedAtAfterPointInTime(afterDateTime, perPageSize);

        return ResponseEntity.noContent().build();
    }
}
