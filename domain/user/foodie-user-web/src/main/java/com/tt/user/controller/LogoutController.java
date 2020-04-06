package com.tt.user.controller;

import com.tt.controller.BaseController;
import com.tt.pojo.JSONResult;
import com.tt.user.service.UserService;
import com.tt.utils.CookieUtils;
import com.tt.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create By Lv.QingYu in 2020/4/6
 */
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping(value = "userCenter")
public class LogoutController extends BaseController {

    private final RedisOperator redisOperator;

    @Autowired
    public LogoutController(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

    /**
     * 用户退出登录，删除用户cookie和购物车缓存
     *
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public JSONResult logout(@RequestParam("userId") String userId,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, USER_LOGIN_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, FOODIE_SHOP_CART);
        String shopCartKey = FOODIE_SHOP_CART + ":" + userId;
        redisOperator.del(shopCartKey);
        return JSONResult.ok();
    }

}
