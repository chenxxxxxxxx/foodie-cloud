package com.tt.order.service.fallback;

import com.tt.item.service.ItemService;
import com.tt.order.service.fallback.factory.ItemServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@FeignClient(value = "foodie-item-service", fallbackFactory = ItemServiceFallbackFactory.class)
public interface ItemServiceFeignClient extends ItemService {
}
