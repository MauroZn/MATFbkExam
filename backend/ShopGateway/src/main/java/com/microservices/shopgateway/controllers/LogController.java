package com.microservices.shopgateway.controllers;

import com.microservices.shopgateway.models.ApiLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Annota la classe come un controller REST
@RequestMapping("/api") // Definisce il prefisso per tutti gli endpoint del controller
@CrossOrigin(origins = "http://localhost:4200") // Permette le richieste cross-origin dal frontend che gira su localhost:4200
public class LogController {

    @Autowired // Inietta automaticamente un'istanza di MongoTemplate per l'accesso al database MongoDB
    private MongoTemplate mongoTemplate;

    // Endpoint GET per ottenere tutti i log memorizzati nel database
    @GetMapping("/logs")
    public List<ApiLog> getLogs() {
        // Recupera tutti i log dalla collezione ApiLog nel database MongoDB
        return mongoTemplate.findAll(ApiLog.class);
    }

}
