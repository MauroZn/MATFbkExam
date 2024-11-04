package com.microservices.catalog.repositories;

import com.microservices.catalog.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByCode(String code);

    List<Product> findByCategory(String category);
}
