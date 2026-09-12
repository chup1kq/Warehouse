package db.warehouse.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProductUpsertDto(
        @NotBlank(message = "Product name must not be blank")
        String name,

        @NotBlank(message = "Product description must not be blank")
        String description,

        @Min(value = 0, message = "Product price must be greater than or equal to 0")
        BigDecimal price,

        @Min(value = 0, message = "Product quantity must be greater than or equal to 0")
        Integer quantity
) {
}