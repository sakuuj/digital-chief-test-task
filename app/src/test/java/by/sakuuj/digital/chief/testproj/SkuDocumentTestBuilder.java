package by.sakuuj.digital.chief.testproj;

import by.sakuuj.digital.chief.testproj.dto.SkuDocumentDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "builder")
public class SkuDocumentTestBuilder {

    private UUID id = UUID.fromString("9aba4152-1751-44d6-9f4e-88bf821258d9");
    private String department = "footwear";
    private String storeLocation = "800 16th St NW, Washington, DC 20006, United States";
    private BigDecimal productPrice = BigDecimal.valueOf(700, 0);
    private short productSize = 43;
    private short version = 444;
    private String createdAt = "2022-12-15T11:27:34";
    private String updatedAt = "2023-01-05T17:28:59";
    private UUID productId = UUID.fromString("46f4e776-888e-4cb8-b370-17282f5b2985");

    public SkuDocumentDto buildDto() {

        return SkuDocumentDto.builder()
                .id(id)
                .department(department)
                .storeLocation(storeLocation)
                .productPrice(productPrice)
                .productSize(productSize)
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .productId(productId)
                .build();
    }
}
