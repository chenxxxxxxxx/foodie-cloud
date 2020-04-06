package com.tt.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.tt.auth.pojo.UserAccount;
import com.tt.auth.service.AuthService;
import com.tt.user.pojo.Users;
import com.tt.user.service.UserService;
import com.tt.utils.JsonUtils;
import com.tt.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * Create By Lv.QingYu in 2020/3/30
 */

@RestController
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String KEY = "FOODIE-AUTH";

    private static final String ISSUER = "lv";

    private static final long TOKEN_EXP_TIME = 64000L;

    private static final String NAME = "username";

    private static final String ROLE = "role";

    private final UserService userService;

    private final RedisOperator redisOperator;

    @Autowired
    public AuthServiceImpl(UserService userService, RedisOperator redisOperator){
        this.userService = userService;
        this.redisOperator = redisOperator;
    }

    @Override
    public UserAccount login(String username, String password) {
        Users users = userService.queryUserForLogin(username, password);
        if(users == null){
            return new UserAccount();
        }
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        String token = JWT.create().withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))
                .withClaim(NAME, username)
                .withClaim(ROLE, "admin")
                .sign(algorithm);
        String refreshToken = UUID.randomUUID().toString();
        UserAccount userAccount = new UserAccount(username, token, refreshToken);
        redisOperator.set(refreshToken, JsonUtils.objectToJson(userAccount));
        return userAccount;
    }

    @Override
    public boolean verify(String token, String username) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(KEY);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).withClaim(NAME, username).build();
            verifier.verify(token);
            return true;
        }catch (Exception e){
            log.error("[token校验服务]-服务异常，用户：{}，异常信息：{}", username, e);
            return false;
        }
    }

    @Override
    public UserAccount refresh(String refreshToken) {
        RBucket<Object> rBucket = redisOperator.getRBucket(refreshToken);
        String userAccountStr = (String) rBucket.get();
        UserAccount userAccount = JsonUtils.jsonToPojo(userAccountStr, UserAccount.class);
        if(userAccount == null){
            return new UserAccount();
        }

        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        String token = JWT.create().withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXP_TIME))
                .withClaim(NAME, userAccount.getUsername())
                .withClaim(ROLE, "admin")
                .sign(algorithm);
        String refreshTokenNew = UUID.randomUUID().toString();
        UserAccount userAccountNew = new UserAccount(userAccount.getUsername(), token, refreshTokenNew);
        redisOperator.set(refreshTokenNew, JsonUtils.objectToJson(userAccountNew));
        redisOperator.del(refreshToken);
        return userAccountNew;
    }

    @Override
    public boolean verifyUserName(String userName) {
        return userService.queryUsernameIsExist(userName);
    }

}
