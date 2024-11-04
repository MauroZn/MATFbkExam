package com.microservices.order.models;

//Id
//Product Id
//Product Title
//Product Category
//Quantity
// price
//userId

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Order {

    @Id
    private String id;
    private String productId;
    private String productTitle;
    private String productCategory;
    private Integer quantity;
    private Double price;
    private String userId;
}
