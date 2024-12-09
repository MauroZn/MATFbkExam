package com.microservices.catalog.services;

import com.microservices.catalog.models.Product;
import com.microservices.catalog.repositories.ProductRepository;
import com.microservices.catalog.services.interfaces.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Indica che questa classe è un servizio Spring e sarà gestita dal container di Spring
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository; // Repository per l'accesso ai dati dei prodotti

    // Costruttore con iniezione delle dipendenze per il repository
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getProducts() {
        // Recupera tutti i prodotti dal database
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(String id) {
        // Cerca un prodotto tramite il suo ID
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getProductByCode(String code) {
        // Cerca un prodotto tramite il suo codice
        return productRepository.findByCode(code);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        // Recupera i prodotti appartenenti a una determinata categoria
        return productRepository.findByCategory(category);
    }

    @Override
    public Product createProduct(Product product) {
        // Salva un nuovo prodotto nel database
        return productRepository.save(product);
    }

    @Override
    public Product updateAvailability(String id, Integer availability) {
        // Aggiorna la disponibilità di un prodotto esistente
        return productRepository.findById(id).map(product -> {
            product.setAvailability(availability); // Modifica la disponibilità del prodotto
            return productRepository.save(product); // Salva il prodotto aggiornato nel database
        }).orElseThrow(
                // Lancia un'eccezione se il prodotto con l'ID specificato non esiste
                () -> new RuntimeException("Product not found with id " + id)
        );
    }
}
