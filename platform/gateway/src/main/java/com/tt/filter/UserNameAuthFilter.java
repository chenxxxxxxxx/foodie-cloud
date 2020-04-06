package com.tt.filter;

import com.tt.client.AuthServiceFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Create By Lv.QingYu in 2020/4/4
 */
@Component("userNameAuthFilter")
public class UserNameAuthFilter implements GatewayFilter, Ordered {

    private static final String USER_NAME = "AUTH_NAME";

    private final AuthServiceFeignClient authServiceFeignClient;

    @Autowired
    @Lazy
    public UserNameAuthFilter(AuthServiceFeignClient authServiceFeignClient){
        this.authServiceFeignClient  = authServiceFeignClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String username = headers.getFirst(USER_NAME);
        if(StringUtils.isBlank(username)){
            response.setStatusCode(HttpStatus.BAD_GATEWAY);
            return response.setComplete();
        }
        boolean verifyUserName = authServiceFeignClient.verifyUserName(username);
        if(!verifyUserName){
            response.setStatusCode(HttpStatus.BAD_GATEWAY);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
