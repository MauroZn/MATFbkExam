package com.microservices.order.services.openfeign;

import com.microservices.order.models.shared.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("catalog")
public interface FeignProductService {

    @RequestMapping(value= "/{productId}",
            method = RequestMethod.GET)
    Product getProduct(@PathVariable String productId);

    @RequestMapping(value= "/{productId}/availability/{quantity}",
            method = RequestMethod.PUT)
    Product updateProductAvailability(
            @PathVariable String productId,
            @PathVariable Integer quantity);
}
