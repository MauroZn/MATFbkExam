package com.microservices.order.repositories;

import com.microservices.order.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

// Interfaccia per l'accesso ai dati degli ordini, estende MongoRepository per fornire metodi CRUD
public interface OrderRepository extends MongoRepository<Order, String> {

    // Metodo per trovare tutti gli ordini di un utente specifico
    List<Order> findByUserId(String userId);

    // Metodo per trovare un ordine specifico in base a userId e id dell'ordine
    Optional<Order> findByUserIdAndId(String userId, String id);

}
