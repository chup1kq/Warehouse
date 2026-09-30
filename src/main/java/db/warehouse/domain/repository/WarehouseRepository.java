package db.warehouse.domain.repository;

import db.warehouse.domain.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarehouseRepository extends MongoRepository<Product, String> {

}
