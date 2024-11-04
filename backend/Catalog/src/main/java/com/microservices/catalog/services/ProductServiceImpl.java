package com.microservices.catalog.services;

import com.microservices.catalog.models.Product;
import com.microservices.catalog.repositories.ProductRepository;
import com.microservices.catalog.services.interfaces.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getProductByCode(String code) {
       return productRepository.findByCode(code);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
       return productRepository.findByCategory(category);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateAvailability(String id, Integer availability) {
        return productRepository.findById(id).map(product -> {
            product.setAvailability(availability);
            return productRepository.save(product);
        }).orElseThrow(
                () -> new RuntimeException(  "product not found with id " + id));

    }
}
