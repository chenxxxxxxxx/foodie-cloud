package com.tt.auth.service;

import com.tt.auth.pojo.UserAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create By Lv.QingYu in 2020/3/30
 */
@FeignClient(value = "foodie-auth-service")
@RequestMapping("/auth-api")
public interface AuthService {

    /**
     * 登录接口- 并返回Token信息
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    UserAccount login(@RequestParam("username") String username,
                      @RequestParam("password") String password);

    @GetMapping("verify")
    boolean verify(@RequestParam("token") String token,
                      @RequestParam("username") String username);

    @GetMapping("refresh")
    UserAccount refresh(@RequestParam("refreshToken") String refreshToken);

    @GetMapping("verifyUserName")
    boolean verifyUserName(@RequestParam("userName") String userName);


}
