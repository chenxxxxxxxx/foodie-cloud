package com.tt.user;

import com.tt.cart.service.CartService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Create By Lv.QingYu in 2020/3/21
 */
@SpringBootApplication
@MapperScan(basePackages = "com.tt.user.mapper")
@ComponentScan(basePackages = {"com.tt", "org.n3r.idworker"})
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients(basePackageClasses = CartService.class)
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}
