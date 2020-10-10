package com.heimdall.feign.demo.controller;

import com.heimdall.feign.demo.client.UserFeign;
import com.heimdall.feign.demo.entity.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author crh
 * @date 2020-09-12
 */
@RestController
@RequestMapping("/request")
@Validated
public class RequestUserController {

    @Resource
    private UserFeign userFeign;

    @RequestMapping("/hello")
    public String feign(String name) {
        return userFeign.getUserByName(name);
    }

    @RequestMapping("/json")
    public User json(Integer id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return userFeign.updateUser(user);
    }

    @RequestMapping("/{id}")
    public String getUserById(@PathVariable Integer id, String name) {
        return userFeign.getUserById(id, name);
    }

}
