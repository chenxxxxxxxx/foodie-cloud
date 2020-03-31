package com.tt.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Create By Lv.QingYu in 2020/3/31
 */
@Component("customAuthFilter")
public class CustomAuthFilter implements GatewayFilter, Ordered {

    private static final String AUTH = "Authorization";
    private static final String USERNAME = "user-name";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(AUTH);
        String username = headers.getFirst(USERNAME);
        if(StringUtils.isBlank(token) || StringUtils.isBlank(username)){
            response.setStatusCode(HttpStatus.BAD_GATEWAY);
            return response.setComplete();
        }

        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header(USERNAME, username);
        mutate.header(AUTH, token);
        ServerHttpRequest serverHttpRequest = mutate.build();

        response.getHeaders().add(USERNAME, username);
        response.getHeaders().add(AUTH, token);
        return chain.filter(exchange.mutate()
                .request(serverHttpRequest)
                .response(response)
                .build());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE-1;
    }
}
