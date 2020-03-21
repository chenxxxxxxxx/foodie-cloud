package com.tt.cart.service.impl;

import com.tt.cart.service.CartService;
import com.tt.pojo.ShopcartBO;
import com.tt.utils.JsonUtils;
import com.tt.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.tt.controller.BaseController.FOODIE_SHOPCART;

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
                                 @RequestBody ShopcartBO shopcartBO) {
        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // 需要判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
        RBucket<Object> bucket = redisOperator.getRBucket(FOODIE_SHOPCART + ":" + userId);
        String shopcartJson =(String) bucket.get();
        List<ShopcartBO> shopcartList = null;
        if (StringUtils.isNotBlank(shopcartJson)) {
            // redis中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopcartList.add(shopcartBO);
            }
        } else {
            // redis中没有购物车
            shopcartList = new ArrayList<>();
            // 直接添加到购物车中
            shopcartList.add(shopcartBO);
        }

        // 覆盖现有redis中的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));

        return true;
    }

    @Override
    public boolean removeItemFromCart(@RequestParam("userId") String userId,
                                      @RequestParam("itemSpecId") String itemSpecId) {
        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        RBucket<Object> rBucket = redisOperator.getRBucket(FOODIE_SHOPCART + ":" + userId);
        String shopcartJson = (String) rBucket.get();
        if (StringUtils.isNotBlank(shopcartJson)) {
            // redis中已经有购物车了
            List<ShopcartBO> shopcartList = JsonUtils.jsonToList(shopcartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话则删除
            for (ShopcartBO sc : shopcartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopcartList.remove(sc);
                    break;
                }
            }
            // 覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        }

        return true;
    }

    @Override
    public boolean clearCart(@RequestParam("userId") String userId) {
        redisOperator.del(FOODIE_SHOPCART + ":" + userId);
        return true;
    }
}
