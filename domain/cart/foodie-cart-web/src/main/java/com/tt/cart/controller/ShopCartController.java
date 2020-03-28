package com.tt.cart.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.tt.cart.service.CartService;
import com.tt.controller.BaseController;
import com.tt.pojo.JSONResult;
import com.tt.pojo.ShopCartBO;
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
                                  @RequestBody ShopCartBO shopcartBO,
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
    @HystrixCommand(
            commandKey = "delShopCart",
            // 全局服务分组，用于组织仪表盘，统计信息。默认：类名
            groupKey = "delShopCartGroup",
            fallbackMethod = "delShopCartFail",
            threadPoolProperties = {
                    // 核心线程数（并发执行的最大线程数，默认10）
                    @HystrixProperty(name = "coreSize", value = "10"),
                    //  #BlockingQueue的最大队列数，默认值-1
                    @HystrixProperty(name = "maxQueueSize", value = "20"),
                    // 在maxQueueSize=-1的时候无效，即使maxQueueSize没有达到最大值，
                    // 达到queueSizeRejectionThreshold该值后，请求也会被拒绝，默认值5
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15")
            })
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

    /**
     * 删除购物车中商品 - 熔断
     * @param userId
     * @param itemSpecId
     * @param request
     * @param response
     * @return
     */
    public JSONResult delShopCartFail(String userId,
                                      String itemSpecId,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        return JSONResult.errorMsg("网络开小差了，稍后再试吧~~~~");
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
        if (result) {
            return JSONResult.ok();
        }
        return JSONResult.errorMsg("清空购物车失败！");
    }

}
