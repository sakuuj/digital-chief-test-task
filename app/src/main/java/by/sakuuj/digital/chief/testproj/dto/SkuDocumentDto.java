package by.sakuuj.digital.chief.testproj.dto;

import by.sakuuj.digital.chief.testproj.json.constants.SkuDocumentDtoPropertyNames;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SkuDocumentDto {

    @JsonIgnore
    private UUID id;

    @JsonProperty(SkuDocumentDtoPropertyNames.DEPARTMENT)
    private String department;

    @JsonProperty(SkuDocumentDtoPropertyNames.STORE_LOCATION)
    private String storeLocation;

    @JsonProperty(SkuDocumentDtoPropertyNames.PRODUCT_PRICE)
    private BigDecimal productPrice;

    @JsonProperty(SkuDocumentDtoPropertyNames.PRODUCT_SIZE)
    private short productSize;

    @JsonProperty(SkuDocumentDtoPropertyNames.VERSION)
    private short version;

    @JsonProperty(SkuDocumentDtoPropertyNames.CREATED_AT)
    private String createdAt;

    @JsonProperty(SkuDocumentDtoPropertyNames.UPDATED_AT)
    private String updatedAt;

    @JsonIgnore
    private UUID productId;

    @JsonSetter(value = SkuDocumentDtoPropertyNames.JOIN_FIELD)
    private void unpackJoinField(Map<String, String> joinField) {

        String parent = joinField.get(SkuDocumentDtoPropertyNames.JOIN_FIELD__PARENT);
        this.productId = UUID.fromString(parent);
    }

    @JsonGetter(SkuDocumentDtoPropertyNames.JOIN_FIELD)
    private Map<String, String> getJoinField() {

        return Map.of(
                SkuDocumentDtoPropertyNames.JOIN_FIELD__NAME, "sku",
                SkuDocumentDtoPropertyNames.JOIN_FIELD__PARENT, productId.toString()
        );
    }
}
