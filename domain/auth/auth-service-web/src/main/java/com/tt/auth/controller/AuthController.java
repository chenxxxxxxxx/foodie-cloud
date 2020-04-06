package com.tt.auth.controller;

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


    @GetMapping("verifyToken")
    public JSONResult verify(@RequestParam("token") String token,
                   @RequestParam("userId") String userId){
        boolean verify = authService.verify(token, userId);
        return JSONResult.ok(verify);
    }

}
