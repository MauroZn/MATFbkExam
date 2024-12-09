package com.microservices.catalog.services.interfaces;

import com.microservices.catalog.models.Product;

import java.util.List;
import java.util.Optional;

// Interfaccia che definisce i metodi per la gestione dei prodotti
// Le rotte documentate rappresentano le operazioni che questa interfaccia supporta

// Elenco dei prodotti: GET /products
// Ottenere un prodotto tramite ID: GET /products/{id}
// Ricerca per categoria: GET /products/category/{category}
// Creare un prodotto: POST /products
// Modificare la disponibilità: PUT /products/{id}/availability/{value}

public interface ProductService {

    // Recupera la lista di tutti i prodotti
    List<Product> getProducts();

    // Recupera un prodotto tramite il suo ID
    Optional<Product> getProductById(String id);

    // Recupera un prodotto tramite il codice
    Optional<Product> getProductByCode(String code);

    // Recupera i prodotti filtrati per una categoria specifica
    List<Product> getProductsByCategory(String category);

    // Crea un nuovo prodotto
    Product createProduct(Product product);

    // Aggiorna la disponibilità di un prodotto specifico
    Product updateAvailability(String id, Integer availability);
}
