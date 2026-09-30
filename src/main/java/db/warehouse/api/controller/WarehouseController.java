package db.warehouse.api.controller;

import db.warehouse.api.dto.Page;
import db.warehouse.api.dto.ProductDto;
import db.warehouse.api.dto.ProductFilter;
import db.warehouse.api.dto.ProductUpsertDto;
import db.warehouse.domain.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Integer quantityMin,
            @RequestParam(required = false) Integer quantityMax,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy
    ) {
        ProductFilter filter = new ProductFilter(
                name,
                description,
                quantityMin,
                quantityMax,
                priceMin,
                priceMax
        );

        return warehouseService.getProducts(filter, page, size, sortBy);
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable String id) {
        return warehouseService.getProduct(id);
    }

    @PostMapping
    public ProductDto createProduct(@Valid @RequestBody ProductUpsertDto product) {
        return warehouseService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ProductDto updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpsertDto product
    ) {
        return warehouseService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        warehouseService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
