package com.tt.cart.controller;

import com.tt.cart.service.CartService;
import com.tt.controller.BaseController;
import com.tt.pojo.JSONResult;
import com.tt.pojo.ShopcartBO;
import com.tt.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车接口", tags = {"购物车接口相关的api"})
@RequestMapping("shopCart")
@RestController
public class ShopCartController extends BaseController {

    private final RedisOperator redisOperator;

    private final CartService cartService;

    @Autowired
    public ShopCartController(RedisOperator redisOperator, CartService cartService) {
        this.redisOperator = redisOperator;
        this.cartService = cartService;
    }

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/addShopCart")
    public JSONResult addShopCart(@RequestParam String userId,
                                  @RequestBody ShopcartBO shopcartBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户还未登录！");
        }
        boolean result = cartService.addItemToCart(userId, shopcartBO);
        if (result) {
            return JSONResult.ok();
        }
        return JSONResult.errorMsg("商品添加购物车失败！");
    }

    @ApiOperation(value = "删除购物车中商品", notes = "删除购物车中商品", httpMethod = "POST")
    @PostMapping("/delShopCart")
    public JSONResult delShopCart(@RequestParam String userId,
                                  @RequestParam String itemSpecId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JSONResult.errorMsg("购物车删除商品失败！");
        }

        boolean result = cartService.removeItemFromCart(userId, itemSpecId);
        if (result) {
            return JSONResult.ok();
        }
        return JSONResult.errorMsg("购物车删除商品失败！");
    }

    @ApiOperation(value = "清空购物车中商品", notes = "清空购物车中商品", httpMethod = "POST")
    @PostMapping("/clearShopCart")
    public JSONResult clearShopCart(@RequestParam("userId") String userId,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户还未登录");
        }
        boolean result = cartService.clearCart(userId);
        if(result){
            return JSONResult.ok();
        }
        return JSONResult.errorMsg("清空购物车失败！");
    }

}
