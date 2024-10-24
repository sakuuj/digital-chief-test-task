package by.sakuuj.digital.chief.testproj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SkuRequest(
        @NotBlank @Length(min = 5, max = 50)
        String department,
        @NotBlank @Length(min = 5, max = 50)
        String storeLocation,
        @NotNull @Digits(integer = 8, fraction = 2) @Positive
        BigDecimal productPrice,
        @Positive
        short productSize
) {
}
