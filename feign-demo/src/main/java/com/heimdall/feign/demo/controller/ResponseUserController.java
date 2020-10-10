package com.heimdall.feign.demo.controller;

import com.heimdall.feign.demo.entity.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author crh
 * @date 2020-09-12
 */
@RestController
@RequestMapping("/response")
public class ResponseUserController {

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello, " + name;
    }

    @PostMapping("/json")
    public User hello(@RequestBody @Validated User user) {
        return user;
    }

    @GetMapping("/{id}")
    public String path(@PathVariable Integer id, String name) {
        return "hello, id: " + id + ", name: " + name;
    }


}
