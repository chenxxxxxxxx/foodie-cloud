package com.tt.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Create By Lv.QingYu in 2020/3/30
 */
@Configuration
public class RedisLimiterConfiguration {
    // 限流速率，每秒发十个令牌
    private static final int USER_REPLENISH_RATE = 10;
    // 令牌桶的容量大小，20个
    private static final int USER_BURST_CAPACITY = 20;

    private static final int ORDER_REPLENISH_RATE = 20;

    private static final int ORDER_BURST_CAPACITY = 50;

    /**
     * 可以通过KeyResolver来指定限流的Key。比如我们需要根据用户来做限流，IP来做限流等等。
     *
     * IP限流
     * @return
     */
    @Bean(value = "ipAndPortResolver")
    @Primary
    public KeyResolver ipAndPortResolver(){
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }

    @Bean(value = "rateLimiterUser")
    @Primary
    public RedisRateLimiter rateLimiterUser(){
        return new RedisRateLimiter(USER_REPLENISH_RATE, USER_BURST_CAPACITY);
    }

    @Bean(value = "rateLimiterOrder")
    public RedisRateLimiter rateLimiterOrder(){
        return new RedisRateLimiter(ORDER_REPLENISH_RATE, ORDER_BURST_CAPACITY);
    }

    @Bean("redisLimiterItem")
    public RedisRateLimiter redisLimiterItem() {
        return new RedisRateLimiter(ORDER_REPLENISH_RATE, ORDER_BURST_CAPACITY);
    }

}
