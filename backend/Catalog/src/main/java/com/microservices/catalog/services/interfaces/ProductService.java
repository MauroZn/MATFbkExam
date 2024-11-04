package com.microservices.catalog.services.interfaces;

import com.microservices.catalog.models.Product;

import java.util.List;
import java.util.Optional;

//List products: /api/products
//Get product by Id: /api/products/{id}
//Search by category: /api/products/category/{category}
//Create product: POST /api/products
//Change availability: PUT /api/products/{id}/availability/{value}


public interface ProductService {

    List<Product> getProducts();
    Optional<Product> getProductById(String id);
    Optional<Product> getProductByCode(String code);
    List<Product> getProductsByCategory(String category);
    Product createProduct(Product product);
    Product updateAvailability(String id, Integer availability);

}
