package by.sakuuj.digital.chief.testproj.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SkuResponse(
        UUID sequenceNumber,
        String department,
        String storeLocation,
        BigDecimal productPrice,
        short productSize,
        short version,
        String createdAt,
        String updatedAt,
        UUID productId
){
}
