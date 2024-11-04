/*
package com.microservices.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.order.models.Order;
import com.microservices.order.models.dto.OrderRequest;
import com.microservices.order.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private String userId = "1111";
    private String productId = "1701";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        orderRepository.deleteAll(); // Clean up the database before each test
    }

    @Test
    public void testGetUserPurchases() throws Exception {
        // Perform GET request to fetch user purchases and capture the result
        MvcResult result = mockMvc.perform(get("/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn(); // Capture the result

        // Get the response content
        String responseContent = result.getResponse().getContentAsString();

        // Print the response content to the console
        System.out.println("Response Content: " + responseContent);
    }

    @Test
    public void testGetPurchase() throws Exception {
        // Add an order to the repository
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(2);
        orderRepository.save(order);

        // Perform GET request to fetch a specific purchase
        mockMvc.perform(get("/{userId}/{productId}", userId, productId))
                .andExpect(status().isOk());
    }

    @Test
    public void testBuy() throws Exception {

        userId = "1111";
        productId = "1701";

        // Create an OrderRequest object
        OrderRequest orderRequest = new OrderRequest(userId, 2, productId);

        // Convert OrderRequest to JSON string
        String requestBody = objectMapper.writeValueAsString(orderRequest);

        // Perform POST request to create a new order
        mockMvc.perform(post("/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // Verify that the order has been saved in the repository
        Optional<Order> savedOrder = orderRepository.findById("1");
        mockMvc.perform(get("/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

    }
}
*/
