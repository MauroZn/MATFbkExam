package com.microservices.catalog.repositories;

import com.microservices.catalog.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

// Interfaccia per il repository dei prodotti, estende MongoRepository
// Fornisce metodi predefiniti per l'interazione con MongoDB, come salvataggio, aggiornamento, eliminazione e ricerca
public interface ProductRepository extends MongoRepository<Product, String> {

    // Metodo personalizzato per trovare un prodotto tramite il codice
    Optional<Product> findByCode(String code);

    // Metodo personalizzato per trovare prodotti appartenenti a una determinata categoria
    List<Product> findByCategory(String category);
}
