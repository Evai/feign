package com.heimdall.feign.demo;

import com.heimdall.feign.springboot.starter.EnableFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClient(basePackages = "com.heimdall.feign.demo.client")
public class FeignDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignDemoApplication.class, args);
    }

}
