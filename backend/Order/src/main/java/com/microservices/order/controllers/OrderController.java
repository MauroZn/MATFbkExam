package com.microservices.order.controllers;

import com.microservices.order.models.Order;
import com.microservices.order.models.dto.OrderRequest;
import com.microservices.order.services.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica che questa classe è un controller REST
// @RequestMapping("/api/purchases") // Route di base per le richieste (commentato quando si utilizza il gateway)
public class OrderController {

    @Autowired // Iniezione automatica del client per la scoperta dei servizi (Eureka)
    DiscoveryClient discoveryClient;

    private final OrderService orderService; // Servizio per la gestione degli ordini

    // Costruttore con iniezione di dipendenze per il servizio degli ordini
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Lista degli acquisti di un utente: GET /purchases/{userId}
    @GetMapping("/{userId}")
    public List<Order> getUserPurchases(@PathVariable String userId) {
        // Restituisce la lista di acquisti di un utente specifico
        return orderService.getUserPurchases(userId);
    }

    // Dettaglio di un acquisto specifico: GET /purchases/{userId}/{id}
    @GetMapping("/{userId}/{id}")
    public Optional<Order> getPurchase(@PathVariable String userId,
                                       @PathVariable String id) {
        // Restituisce i dettagli di un acquisto specifico
        return orderService.getPurchase(userId, id);
    }

    // Effettua un acquisto: POST /purchases/buy
    @PostMapping("/buy")
    public Optional<Order> buy(@RequestBody OrderRequest orderRequest) {
        // Chiama il servizio per completare un acquisto con i dettagli forniti nella richiesta
        return orderService.buy(
                orderRequest.getUserId(),
                orderRequest.getCount(),
                orderRequest.getProductId());
    }

    // Endpoint di test per visualizzare le istanze registrate del servizio "catalog"
    @GetMapping("/test")
    public String test() {
        // Stampa a console le istanze del servizio "catalog" (per test/debug)
        discoveryClient.getInstances("catalog").forEach(i -> System.out.println(i.getClass()));
        return "test";
    }
}
