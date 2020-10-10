package com.heimdall.feign.demo.controller;

import com.heimdall.feign.demo.client.GithubFeign;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author crh
 * @date 2020-09-12
 */
@RestController
@RequestMapping("/github")
public class GithubController {

    @Resource
    private GithubFeign githubFeign;

    @GetMapping("/user")
    public String getUser(String user) {
        return githubFeign.getUser(user);
    }

}
