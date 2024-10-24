package by.sakuuj.digital.chief.testproj.dto;

import by.sakuuj.digital.chief.testproj.SkuTestBuilder;
import by.sakuuj.digital.chief.testproj.configs.JacksonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

class SkuDocumentDtoJsonMappingTest {

    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();

    @Test
    void shouldSerializeSkuDocumentDtoToExpected() throws JsonProcessingException, JSONException {

        // given
        var dto = SkuTestBuilder.builder().buildDocumentDto();

        String expectedDto = """
                {
                    "sku_department": "%s",
                    "sku_store_location": "%s",
                    "sku_product_price": %s,
                    "sku_product_size": %s,
                    "version": %s,
                    "created_at": "%s",
                    "updated_at": "%s",
                    "join_field": {
                        "name": "sku",
                        "parent": "%s"
                    }
                }
                """.formatted(
                dto.getDepartment(),
                dto.getStoreLocation(),
                dto.getProductPrice(),
                dto.getProductSize(),
                dto.getVersion(),
                dto.getCreatedAt(),
                dto.getUpdatedAt(),
                dto.getProductId()
        );

        // when
        String actual = objectMapper.writeValueAsString(dto);

        // then
        JSONAssert.assertEquals(expectedDto, actual, false);
    }

    @Test
    void shouldDeserializeProductDocumentToExpectedProductDocumentDto() throws JsonProcessingException, JSONException {

        // given
        var expected = SkuTestBuilder.builder().buildDocumentDto();

        String jsonToDeserialize = """
                {
                    "sku_department": "%s",
                    "sku_store_location": "%s",
                    "sku_product_price": %s,
                    "sku_product_size": %s,
                    "version": %s,
                    "created_at": "%s",
                    "updated_at": "%s",
                    "join_field": {
                        "name": "sku",
                        "parent": "%s"
                    }
                }
                """.formatted(
                expected.getDepartment(),
                expected.getStoreLocation(),
                expected.getProductPrice(),
                expected.getProductSize(),
                expected.getVersion(),
                expected.getCreatedAt(),
                expected.getUpdatedAt(),
                expected.getProductId()
        );

        // when
        SkuDocumentDto actual = objectMapper.readValue(jsonToDeserialize, SkuDocumentDto.class);

        // then
        Assertions.assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}
