package com.microservices.catalog.controllers;

import com.microservices.catalog.models.Product;
import com.microservices.catalog.services.interfaces.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica che questa classe è un controller REST
//@RequestMapping("/products") // Percorso base commentato, può essere utile per organizzare meglio le rotte
public class ProductController {

    private final ProductService productService; // Servizio per la gestione dei prodotti

    // Costruttore che inizializza il servizio dei prodotti tramite iniezione delle dipendenze
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("") // Mappa una richiesta GET alla radice per ottenere tutti i prodotti
    public List<Product> getProducts() {
        return productService.getProducts(); // Recupera la lista di tutti i prodotti
    }

    @GetMapping("/{id}") // Mappa una richiesta GET per ottenere un prodotto specifico tramite ID
    public Optional<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id); // Recupera un prodotto specifico tramite il suo ID
    }

    @GetMapping("/category/{category}") // Mappa una richiesta GET per ottenere prodotti di una categoria specifica
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category); // Recupera prodotti filtrati per categoria
    }

    @GetMapping("/code/{code}") // Mappa una richiesta GET per ottenere un prodotto tramite un codice specifico
    public Optional<Product> getProductByCode(@PathVariable String code) {
        return productService.getProductByCode(code); // Recupera un prodotto specifico tramite il suo codice
    }

    @PostMapping("") // Mappa una richiesta POST per creare un nuovo prodotto
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product); // Crea un nuovo prodotto utilizzando i dati inviati nel corpo della richiesta
    }

    @PutMapping("/{id}/availability/{value}") // Mappa una richiesta PUT per aggiornare la disponibilità di un prodotto
    public Product updateAvailability(@PathVariable String id,
                                      @PathVariable Integer value) {
        return productService.updateAvailability(id, value); // Aggiorna la disponibilità del prodotto specificato
    }

}
