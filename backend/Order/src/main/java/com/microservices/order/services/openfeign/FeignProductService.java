package com.microservices.order.services.openfeign;

import com.microservices.order.models.shared.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// Interfaccia Feign per la comunicazione con il servizio "catalog"
@FeignClient("catalog") // Indica che questo client Feign si connette al servizio registrato come "catalog"
public interface FeignProductService {

    // Metodo per ottenere le informazioni di un prodotto specifico
    @RequestMapping(value = "/{productId}",
            method = RequestMethod.GET) // Mappa una richiesta GET all'endpoint del servizio "catalog"
    Product getProduct(@PathVariable String productId); // `productId` è il parametro del percorso

    // Metodo per aggiornare la disponibilità di un prodotto specifico
    @RequestMapping(value = "/{productId}/availability/{quantity}",
            method = RequestMethod.PUT) // Mappa una richiesta PUT per aggiornare la disponibilità
    Product updateProductAvailability(
            @PathVariable String productId, // `productId` è il parametro del percorso
            @PathVariable Integer quantity); // `quantity` rappresenta il nuovo valore di disponibilità
}
