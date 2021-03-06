package com.tt.order.config;

import com.tt.order.config.properties.CustomRedisProperties;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create By Lv.QingYu in 2020/3/21
 *
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(CustomRedisProperties.class)
public class RedissonConfig {

    private final CustomRedisProperties customRedisProperties;

    @Autowired
    public RedissonConfig(CustomRedisProperties customRedisProperties){
        this.customRedisProperties = customRedisProperties;
    }



    /**
     * 单机模式自动装配
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="redisson.address")
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(customRedisProperties.getAddress())
                .setTimeout(customRedisProperties.getTimeout())
                .setConnectionPoolSize(customRedisProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(customRedisProperties.getConnectionMinimumIdleSize());

        if(StringUtils.isNotBlank(customRedisProperties.getPassword())) {
            serverConfig.setPassword(customRedisProperties.getPassword());
        }
        return Redisson.create(config);
    }

}
