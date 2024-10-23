package by.sakuuj.digital.chief.testproj.dto;

import by.sakuuj.digital.chief.testproj.json.constants.ProductDocumentDtoPropertyNames;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder
public record ProductDocumentDto(

        @JsonIgnore
        UUID id,

        @JsonProperty(ProductDocumentDtoPropertyNames.TITLE)
        String title,

        @JsonProperty(ProductDocumentDtoPropertyNames.DESCRIPTION)
        String description,

        @JsonProperty(ProductDocumentDtoPropertyNames.TYPE)
        String type,

        @JsonProperty(ProductDocumentDtoPropertyNames.BRAND)
        String brand,

        @JsonProperty(ProductDocumentDtoPropertyNames.VERSION)
        short version,

        @JsonProperty(ProductDocumentDtoPropertyNames.CREATED_AT)
        String createdAt,

        @JsonProperty(ProductDocumentDtoPropertyNames.UPDATED_AT)
        String updatedAt
) {

    @JsonProperty(value = ProductDocumentDtoPropertyNames.JOIN_FIELD, access = JsonProperty.Access.READ_ONLY)
    private String joinField() {
        return "product";
    }
}
