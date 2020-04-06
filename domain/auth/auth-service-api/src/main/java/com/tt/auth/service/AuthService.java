package com.tt.auth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create By Lv.QingYu in 2020/3/30
 */
@FeignClient(value = "foodie-auth-service")
@RequestMapping("/auth-api")
public interface AuthService {


    @GetMapping("verify")
    boolean verify(@RequestParam("token") String token,
                      @RequestParam("userId") String userId);

}
