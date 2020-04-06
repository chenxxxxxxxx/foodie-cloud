package com.tt.user.service;

import com.tt.user.pojo.Users;
import com.tt.user.pojo.bo.UserBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "foodie-user-service")
@RequestMapping("user-api")
public interface UserService {

    /**
     * 判断用户名是否存在
     */
    @GetMapping("user/exists")
    boolean queryUsernameIsExist(@RequestParam("username") String username);

    /**
     * 判断用户名是否存在
     */
    @PostMapping("user")
    Users createUser(@RequestBody UserBO userBO);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    @GetMapping("verify")
    Users queryUserForLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password);

    @GetMapping("userId/exists")
    boolean queryUserIdIsExist(@RequestParam("userId") String userId);
}
