package db.warehouse.domain.service;

import db.warehouse.api.dto.Page;
import db.warehouse.api.dto.ProductDto;
import db.warehouse.api.dto.ProductFilter;
import db.warehouse.api.dto.ProductUpsertDto;
import db.warehouse.domain.entity.Product;
import db.warehouse.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final MongoTemplate mongoTemplate;

    public Page<ProductDto> getProducts(ProductFilter filter, int page, int size, String sortBy) {
        Query query = new Query();

        if (filter.name() != null) {
            query.addCriteria(
                    Criteria.where("name").regex(filter.name(), "i")
            );
        }

        if (filter.description() != null) {
            query.addCriteria(
                    Criteria.where("description").regex(filter.description(), "i")
            );
        }

        if (filter.minQuantity() != null || filter.maxQuantity() != null) {
            Criteria quantityCriteria = Criteria.where("quantity");

            if (filter.minQuantity() != null) {
                quantityCriteria.gte(filter.minQuantity());
            }

            if (filter.maxQuantity() != null) {
                quantityCriteria.lte(filter.maxQuantity());
            }

            query.addCriteria(quantityCriteria);
        }

        if (filter.minPrice() != null || filter.maxPrice() != null) {
            Criteria priceCriteria = Criteria.where("price");

            if (filter.minPrice() != null) {
                priceCriteria.gte(filter.minPrice());
            }

            if (filter.maxPrice() != null) {
                priceCriteria.lte(filter.maxPrice());
            }

            query.addCriteria(priceCriteria);
        }

        int totalElements = (int) mongoTemplate.count(query, Product.class);

        if (sortBy != null && !sortBy.isBlank()) {
            List<Sort.Order> orders = new ArrayList<>();

            for (String value : sortBy.split(";")) {
                String[] parts = value.split(",");

                String field = parts[0];
                Sort.Direction direction =
                        Sort.Direction.fromString(parts[1]);

                orders.add(new Sort.Order(direction, field));
            }

            query.with(Sort.by(orders));
        }

        query.skip((long) page * size);
        query.limit(size);

        List<Product> products = mongoTemplate.find(query, Product.class);

        List<ProductDto> content = products.stream()
                .map(Product::toDto)
                .toList();

        return Page.of(
                content,
                page,
                size,
                totalElements
        );
    }

    public ProductDto getProduct(String id) {
        Product product = warehouseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found")
        );

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    public ProductDto createProduct(ProductUpsertDto product) {
        Product pr = warehouseRepository.save(Product.builder()
                .name(product.name())
                .description(product.description())
                .price(product.price())
                .quantity(product.quantity())
                .build()
        );

        return new ProductDto(pr.getId(), pr.getName(), pr.getDescription(), pr.getPrice(), pr.getQuantity());
    }

    public ProductDto updateProduct(String id, ProductUpsertDto product) {
        Product existingProduct = warehouseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found")
        );

        existingProduct.setName(product.name());
        existingProduct.setDescription(product.description());
        existingProduct.setPrice(product.price());
        existingProduct.setQuantity(product.quantity());

        Product updatedProduct = warehouseRepository.save(existingProduct);

        return new ProductDto(
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice(),
                updatedProduct.getQuantity()
        );
    }

    public void deleteProduct(String id) {
        warehouseRepository.deleteById(id);
    }
}
