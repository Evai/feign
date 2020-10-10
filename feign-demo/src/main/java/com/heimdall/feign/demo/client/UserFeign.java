package com.heimdall.feign.demo.client;


import com.heimdall.feign.core.PathVariable;
import com.heimdall.feign.core.RequestBody;
import com.heimdall.feign.core.RequestMapping;
import com.heimdall.feign.demo.entity.User;
import com.heimdall.feign.springboot.starter.FeignClient;

/**
 * @author crh
 * @date 2020-09-12
 */
@FeignClient(url = "${response.user.url}", fallbackFactory = UserFeignFallbackFactory.class, headers = {"cccc:wwww", "aaa:ddd"})
public interface UserFeign {

    @RequestMapping(value = "/hello", headers = "aaa:bbbb")
    String getUserByName(String name);

    @RequestMapping(value = "/json", methodType = RequestMapping.MethodType.POST)
    User updateUser(@RequestBody User user);

    @RequestMapping(value = "/{id}", headers = "aaa:bbbb")
    String getUserById(@PathVariable Integer id, String name);

}
