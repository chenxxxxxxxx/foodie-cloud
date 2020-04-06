package com.tt.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.tt.auth.service.AuthService;
import com.tt.user.service.UserService;
import com.tt.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create By Lv.QingYu in 2020/3/30
 */

@RestController
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String KEY = "FOODIE-AUTH";
    private static final String ISSUER = "lv";
    private static final String USER_ID = "userId";
    private static final String ROLE = "role";

    private final UserService userService;

    private final RedisOperator redisOperator;

    @Autowired
    public AuthServiceImpl(UserService userService, RedisOperator redisOperator){
        this.userService = userService;
        this.redisOperator = redisOperator;
    }


    @Override
    public boolean verify(String token, String userId) {
        if(StringUtils.isBlank(token) || StringUtils.isBlank(userId)){
            log.warn("[Token权限校验]-Token:{}或用户UserId:{}为空，无权限访问", token, userId);
            return false;
        }
        try{
            RBucket<Object> rBucket = redisOperator.getRBucket(token);
            if(rBucket == null){
                log.warn("[Token权限校验]-根据Token:{}获取用户UserId:{}的缓存信息失败,无权限访问", token, userId);
                return false;
            }
            Object userInfoObj =  rBucket.get();
            if(userInfoObj == null){
                log.warn("[Token权限校验]-根据Token:{}获取用户UserId:{}的缓存信息失败,无权限访问", token, userId);
                return false;
            }
            boolean userIsExist = userService.queryUserIdIsExist(userId);
            if(!userIsExist){
                log.warn("[Token权限校验]-根据UserId:{}未获取到用户信息，Token:{},无权限访问", userId, token);
            }
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .withClaim(USER_ID, userId)
                    .withClaim(ROLE, "admin")
                    .build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            log.error("[token校验服务]-服务异常，用户：{}，异常信息：{}", userId, e);
            return false;
        }
    }

}
