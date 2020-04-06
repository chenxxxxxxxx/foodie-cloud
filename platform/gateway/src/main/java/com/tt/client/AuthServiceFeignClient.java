package com.tt.client;

import com.tt.auth.service.AuthService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Create By Lv.QingYu in 2020/4/5
 */
@FeignClient(value = "foodie-auth-service")
public interface AuthServiceFeignClient extends AuthService {
}
