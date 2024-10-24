package by.sakuuj.digital.chief.testproj.dto;

import by.sakuuj.digital.chief.testproj.ProductTestBuilder;
import by.sakuuj.digital.chief.testproj.configs.JacksonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;


class ProductDocumentDtoJsonMappingTest {

    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();

    @Test
    void shouldSerializeProductDocumentDtoToExpected() throws JsonProcessingException, JSONException {

        // given
        var dto = ProductTestBuilder.builder().buildDocumentDto();

        String expectedDto = """
                {
                    "product_title": "%s",
                    "product_description": "%s",
                    "product_type": "%s",
                    "product_brand": "%s",
                    "version": %s,
                    "created_at": "%s",
                    "updated_at": "%s",
                    "join_field": "product"
                }
                """.formatted(
                dto.title(),
                dto.description(),
                dto.type(),
                dto.brand(),
                dto.version(),
                dto.createdAt(),
                dto.updatedAt()
        );

        // when
        String actual = objectMapper.writeValueAsString(dto);

        // then
        JSONAssert.assertEquals(expectedDto, actual, false);
    }

    @Test
    void shouldDeserializeProductDocumentToExpectedProductDocumentDto() throws JsonProcessingException, JSONException {

        // given
        var expected = ProductTestBuilder.builder().buildDocumentDto();

        String jsonToDeserialize = """
                {
                    "product_title": "%s",
                    "product_description": "%s",
                    "product_type": "%s",
                    "product_brand": "%s",
                    "version": %s,
                    "created_at": "%s",
                    "updated_at": "%s",
                    "join_field": "product"
                }
                """.formatted(
                expected.title(),
                expected.description(),
                expected.type(),
                expected.brand(),
                expected.version(),
                expected.createdAt(),
                expected.updatedAt()
        );

        // when
        ProductDocumentDto actual = objectMapper.readValue(jsonToDeserialize, ProductDocumentDto.class);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }
}