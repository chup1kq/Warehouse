package db.warehouse.api.dto;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        String description,
        Integer minQuantity,
        Integer maxQuantity,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
