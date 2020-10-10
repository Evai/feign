# feign
基于JDK动态代理实现一个简单的feign，目前仅支持GET和POST，支持自定义header，restful、JSON请求格式。

#### userFeign

```java
@FeignClient(url = "${github.url}", fallbackFactory = UserFeignFallbackFactory.class, headers = {"cccc:wwww", "aaa:ddd"})
public interface GithubFeign {

    @RequestMapping(value = "/users/{user}", headers = "aaa:bbbb")
    String getUser(@PathVariable String user);

}

```

#### application.properties

```properties
github.url=https://api.github.com
```

#### controller
```java
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
```


