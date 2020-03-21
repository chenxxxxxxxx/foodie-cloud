package com.tt.cart.service;

import com.tt.pojo.ShopcartBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create By Lv.QingYu in 2020/3/21
 */
@FeignClient(value = "foodie-cart-service")
@RequestMapping("cart-api")
public interface CartService {

    @PostMapping("addItem")
    boolean addItemToCart(@RequestParam("userId") String userId,
                          @RequestBody ShopcartBO shopcartBO);

    @PostMapping("removeItem")
    boolean removeItemFromCart(@RequestParam("userId") String userId,
                               @RequestParam("itemSpecId") String itemSpecId);

    @PostMapping("clearCart")
    boolean clearCart(@RequestParam("userId") String userId);

}
