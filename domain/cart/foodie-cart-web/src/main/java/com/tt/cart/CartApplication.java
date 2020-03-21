package com.tt.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Create By Lv.QingYu in 2020/3/21
 */
@SpringBootApplication
@MapperScan(basePackages = "com.tt.cart.mapper")
@ComponentScan(basePackages = {"com.tt", "org.n3r.idworker"})
@EnableDiscoveryClient
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

}
