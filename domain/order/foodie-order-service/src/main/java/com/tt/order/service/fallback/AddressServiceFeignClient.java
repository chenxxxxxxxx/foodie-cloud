package com.tt.order.service.fallback;

import com.tt.order.service.fallback.factory.AddressServiceFallbackFactory;
import com.tt.user.service.AddressService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Create By Lv.QingYu in 2020/3/28
 */
@FeignClient(value = "foodie-user-service", fallbackFactory = AddressServiceFallbackFactory.class)
public interface AddressServiceFeignClient extends AddressService {
}
