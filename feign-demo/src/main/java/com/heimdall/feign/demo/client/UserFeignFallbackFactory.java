package com.heimdall.feign.demo.client;

import com.fasterxml.jackson.core.JsonParseException;
import com.heimdall.feign.core.IFallbackFactory;
import com.heimdall.feign.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author crh
 * @date 2020-09-12
 */
@Component
@Slf4j
public class UserFeignFallbackFactory implements IFallbackFactory<UserFeign> {

    @Override
    public UserFeign create(Throwable throwable) {
        return new UserFeign() {
            @Override
            public String getUserByName(String name) {
                throw new RuntimeException(throwable.getMessage());
            }

            @Override
            public User updateUser(User user) {
                throw new RuntimeException(throwable.getMessage());
            }

            @Override
            public String getUserById(Integer id, String name) {
                throw new RuntimeException(throwable.getMessage());
            }
        };
    }
}
