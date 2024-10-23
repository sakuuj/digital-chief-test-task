package by.sakuuj.digital.chief.testproj.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record SkuRequest(
        @NotBlank @Length(min = 5, max = 50)
        String department,
        @NotBlank @Length(min = 5, max = 50)
        String storeLocation,
        @NotNull @Digits(integer = 8, fraction = 2)
        BigDecimal productPrice,
        @Positive
        short productSize
) {
}
