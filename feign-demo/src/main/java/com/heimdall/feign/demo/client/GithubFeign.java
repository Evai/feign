package com.heimdall.feign.demo.client;


import com.heimdall.feign.core.PathVariable;
import com.heimdall.feign.core.RequestMapping;
import com.heimdall.feign.springboot.starter.FeignClient;

/**
 * @author crh
 * @date 2020-09-12
 */
@FeignClient(url = "${github.url}", fallbackFactory = UserFeignFallbackFactory.class, headers = {"cccc:wwww", "aaa:ddd"})
public interface GithubFeign {

    @RequestMapping(value = "/users/{user}", headers = "aaa:bbbb")
    String getUser(@PathVariable String user);

}
