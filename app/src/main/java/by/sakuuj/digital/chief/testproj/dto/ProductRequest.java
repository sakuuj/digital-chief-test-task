package by.sakuuj.digital.chief.testproj.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductRequest(
        @NotBlank @Length(min = 5, max = 200)
        String title,
        @NotBlank @Length(min = 5, max = 500)
        String description,
        @NotBlank @Length(min = 5, max = 100)
        String type,
        @NotBlank @Length(min = 5, max = 50)
        String brand
) {
}
