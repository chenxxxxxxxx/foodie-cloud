package com.tt.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.tt.cart.service.CartService;
import com.tt.controller.BaseController;
import com.tt.pojo.JSONResult;
import com.tt.pojo.ShopCartBO;
import com.tt.user.pojo.Users;
import com.tt.user.pojo.bo.UserBO;
import com.tt.user.pojo.vo.UserVO;
import com.tt.user.service.UserService;
import com.tt.utils.CookieUtils;
import com.tt.utils.JsonUtils;
import com.tt.utils.MD5Utils;
import com.tt.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    private static final String KEY = "FOODIE-AUTH";
    private static final String ISSUER = "lv";
    private static final long TOKEN_EXP_TIME = 64000L;
    private static final String USER_ID = "userId";
    private static final String ROLE = "role";

    private final UserService userService;
    private final RedisOperator redisOperator;
    private final CartService cartService;

    @Autowired
    public PassportController(UserService userService, RedisOperator redisOperator, CartService cartService) {
        this.userService = userService;
        this.redisOperator = redisOperator;
        this.cartService = cartService;
    }

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名不能为空");
        }
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }
        return JSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/register")
    public JSONResult register(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPwd)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已经存在");
        }
        if (password.length() < 6) {
            return JSONResult.errorMsg("密码长度不能少于6");
        }
        if (!password.equals(confirmPwd)) {
            return JSONResult.errorMsg("两次密码输入不一致");
        }
        Users userResult = userService.createUser(userBO);
        if(userResult == null){
            return JSONResult.errorMsg("用户注册失败");
        }
        //setUserInfoCache(userResult, request, response);
        // 同步购物车数据
        //synShopCartData(userResult.getId(), request, response);
        return JSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    @HystrixCommand(
            commandKey = "loginFail",
            // 全局服务分组，用于组织仪表盘，统计信息。默认：类名
            groupKey = "loginFailGroup",
            fallbackMethod = "loginFail",
            threadPoolProperties = {
                    // 核心线程数（并发执行的最大线程数，默认10）
                    @HystrixProperty(name = "coreSize", value = "10"),
                    //  #BlockingQueue的最大队列数，默认值-1
                    @HystrixProperty(name = "maxQueueSize", value = "20"),
                    // 在maxQueueSize=-1的时候无效，即使maxQueueSize没有达到最大值，
                    // 达到queueSizeRejectionThreshold该值后，请求也会被拒绝，默认值5
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15")
            })
    public JSONResult login(@RequestBody UserBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            return JSONResult.errorMsg("用户名或密码不正确");
        }
        UserVO userVO = setUserInfoCache(userResult, request, response);
        // 同步购物车数据
        synShopCartData(userResult.getId(), request, response);
        return JSONResult.ok(userVO);
    }

    /**
     * login - 登录接口服务降级方法
     *
     * @param userBO
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public JSONResult loginFail(UserBO userBO,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        return JSONResult.errorMsg("网络开小差了~~~~");
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synShopCartData(String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        String shopCartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOP_CART, true);
        String shopCartJsonRedis = cartService.synShopCartData(userId, shopCartStrCookie);
        if(StringUtils.isNotBlank(shopCartJsonRedis)){
            CookieUtils.setCookie(request, response, FOODIE_SHOP_CART, shopCartJsonRedis, true);
        }
    }

    public UserVO setUserInfoCache(Users userResult, HttpServletRequest request, HttpServletResponse response){
        String token = createToken(userResult.getId());
        UserVO userVO = buildUserVO(userResult, token);
        String userResultStr = buildUserVOStr(userVO);
        CookieUtils.setCookie(request, response, USER_LOGIN_COOKIE_NAME, userResultStr, true);
        redisOperator.set(token, userResultStr);
        return userVO;
    }

    private String buildUserVOStr(UserVO userVO){
        String userResultStr = JsonUtils.objectToJson(userVO);
        return userResultStr;
    }

    private UserVO buildUserVO(Users users, String token){
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users, userVO);
        String resultId = users.getId();
        userVO.setUserId(resultId);
        userVO.setToken(token);
        return userVO;
    }

    private String createToken(String userId) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        String token = JWT.create().withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))
                .withClaim(USER_ID, userId)
                .withClaim(ROLE, "admin")
                .sign(algorithm);
        return token;
    }

}
