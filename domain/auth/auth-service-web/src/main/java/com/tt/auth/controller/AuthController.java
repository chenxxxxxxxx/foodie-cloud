package com.tt.auth.controller;

import com.tt.auth.pojo.UserAccount;
import com.tt.auth.service.AuthService;
import com.tt.pojo.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Create By Lv.QingYu in 2020/3/30
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    /**
     * 登录接口- 并返回Token信息
     * @param username
     * @param password
     * @return
     */
    @PostMapping("createToken")
    public JSONResult login(@RequestParam("username") String username,
                            @RequestParam("password") String password){
        UserAccount userAccount = authService.login(username, password);
        return JSONResult.ok(userAccount);
    }

    @GetMapping("verifyToken")
    public JSONResult verify(@RequestParam("token") String token,
                   @RequestParam("username") String username){
        boolean verify = authService.verify(token, username);
        return JSONResult.ok(verify);
    }

    @GetMapping("refreshToken")
    public JSONResult refresh(@RequestParam("refreshToken") String refreshToken){
        UserAccount userAccount = authService.refresh(refreshToken);
        return JSONResult.ok(userAccount);
    }
}
