package by.sakuuj.digital.chief.testproj;

import by.sakuuj.digital.chief.testproj.dto.ProductDocumentDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "builder")
public class ProductDocumentTestBuilder {

    private UUID id = UUID.fromString("31b20a1c-b483-486b-a5f0-910b2123b845");
    private String title = "Nike Sneakers Super Blue Omega Cool";
    private String description = "An elegant pair of sea-blue sneakers for an affordable price.";
    private String type = "sneakers";
    private String brand = "Nike";
    private short version = 555;
    private String createdAt = "2021-11-15T19:30:01";
    private String updatedAt = "2021-12-15T20:33:11";

    public ProductDocumentDto buildDto() {

        return ProductDocumentDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .type(type)
                .brand(brand)
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

}

