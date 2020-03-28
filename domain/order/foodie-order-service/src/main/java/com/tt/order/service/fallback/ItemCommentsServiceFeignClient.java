package com.tt.order.service.fallback;

import com.tt.item.service.ItemCommentsService;
import com.tt.order.service.fallback.factory.ItemCommentsServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@FeignClient(value = "foodie-item-service", fallbackFactory = ItemCommentsServiceFallbackFactory.class)
public interface ItemCommentsServiceFeignClient extends ItemCommentsService {
}
