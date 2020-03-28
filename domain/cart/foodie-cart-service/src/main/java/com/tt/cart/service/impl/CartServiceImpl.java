package com.tt.cart.service.impl;

import com.tt.cart.service.CartService;
import com.tt.pojo.ShopCartBO;
import com.tt.utils.JsonUtils;
import com.tt.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.tt.controller.BaseController.FOODIE_SHOP_CART;

/**
 * Create By Lv.QingYu in 2020/3/21
 */
@RestController
@Slf4j
public class CartServiceImpl implements CartService {

    private final RedisOperator redisOperator;

    @Autowired
    public CartServiceImpl(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    @Override
    public boolean addItemToCart(@RequestParam("userId") String userId,
                                 @RequestBody ShopCartBO shopcartBO) {
        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // 需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
        String foodieShopCarKey = FOODIE_SHOP_CART + ":" + userId;
        RBucket<Object> bucket = redisOperator.getRBucket(foodieShopCarKey);
        String shopCartJson =(String) bucket.get();
        List<ShopCartBO> shopCartList = null;
        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis中已经有购物车了
            shopCartList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopCartBO sc : shopCartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopCartList.add(shopcartBO);
            }
        } else {
            // redis中没有购物车
            shopCartList = new ArrayList<>();
            // 直接添加到购物车中
            shopCartList.add(shopcartBO);
        }
        // 覆盖现有redis中的购物车
        redisOperator.set(foodieShopCarKey, JsonUtils.objectToJson(shopCartList));
        return true;
    }

    @Override
    public boolean removeItemFromCart(@RequestParam("userId") String userId,
                                      @RequestParam("itemSpecId") String itemSpecId) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("方法执行了++++++++++++++++++");
        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String foodieShopCarKey = FOODIE_SHOP_CART + ":" + userId;
        RBucket<Object> rBucket = redisOperator.getRBucket(foodieShopCarKey);
        String shopCartJson = (String) rBucket.get();
        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis中已经有购物车了
            List<ShopCartBO> shopCartList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话则删除
            for (ShopCartBO sc : shopCartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopCartList.remove(sc);
                    break;
                }
            }
            // 覆盖现有redis中的购物车
            redisOperator.set(foodieShopCarKey, JsonUtils.objectToJson(shopCartList));
        }
        return true;
    }

    @Override
    public boolean clearCart(@RequestParam("userId") String userId) {
        String foodieShopCarKey = FOODIE_SHOP_CART + ":" + userId;
        redisOperator.del(foodieShopCarKey);
        return true;
    }
}
