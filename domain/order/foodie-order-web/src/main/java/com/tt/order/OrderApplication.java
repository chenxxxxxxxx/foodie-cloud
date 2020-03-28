package com.tt.order;

import com.tt.item.service.ItemService;
import com.tt.order.service.fallback.AddressServiceFeignClient;
import com.tt.order.service.fallback.ItemCommentsServiceFeignClient;
import com.tt.order.service.fallback.ItemServiceFeignClient;
import com.tt.user.service.AddressService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by 半仙.
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.tt.order.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.tt", "org.n3r.idworker"})
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {
        ItemCommentsServiceFeignClient.class,
        AddressServiceFeignClient.class,
        ItemServiceFeignClient.class
})
@EnableScheduling
@EnableCircuitBreaker
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
