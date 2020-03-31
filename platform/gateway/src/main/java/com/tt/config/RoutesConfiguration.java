package com.tt.config;

import com.tt.filter.CustomAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Create By Lv.QingYu in 2020/3/30
 *
 * @Description 路由规则处理类
 */
@Configuration
public class RoutesConfiguration {

    private final KeyResolver ipAndPortResolver;

    @Qualifier("rateLimiterUser")
    private final RedisRateLimiter rateLimiterUser;

    @Qualifier("rateLimiterOrder")
    private final RedisRateLimiter rateLimiterOrder;

    private final CustomAuthFilter customAuthFilter;

    @Autowired
    public RoutesConfiguration(KeyResolver ipAndPortResolver, RedisRateLimiter rateLimiterUser, RedisRateLimiter rateLimiterOrder, CustomAuthFilter customAuthFilter){
        this.ipAndPortResolver = ipAndPortResolver;
        this.rateLimiterUser = rateLimiterUser;
        this.rateLimiterOrder = rateLimiterOrder;
        this.customAuthFilter = customAuthFilter;
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder locatorBuilder) {
        return locatorBuilder
                .routes()
                .route(r -> r.path("/address/**", "/passport/**", "/center/**", "/userInfo/**")
                        .filters(f -> f.requestRateLimiter(config -> {
                            config.setKeyResolver(ipAndPortResolver);
                            config.setRateLimiter(rateLimiterUser);
                            config.setStatusCode(HttpStatus.BAD_GATEWAY);
                        }).filter(customAuthFilter))
                        .uri("lb://FOODIE-USER-SERVICE")
                )
                .route(r -> r.path("/orders/**", "/mycomments/**", "/myorders/**")
                        .filters(f -> f.requestRateLimiter(config -> {
                            config.setKeyResolver(ipAndPortResolver);
                            config.setRateLimiter(rateLimiterOrder);
                            config.setStatusCode(HttpStatus.BAD_GATEWAY);
                        }))
                        .uri("lb://FOODIE-ORDER-SERVICE")
                )
                .route(r -> r.path("/items/**")
                        .uri("lb://FOODIE-ITEM-SERVICE")
                )
                .route(r -> r.path("/shopCart/**")
                        .uri("lb://FOODIE-CART-SERVICE"))
                .build();
    }
}
