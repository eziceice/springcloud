package com.springboot.mall.service.hystrix;

import com.springboot.mall.pojo.User;
import com.springboot.mall.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceHystrix implements UserService {
    @Override
    public User getUser(Long id) {
        return null;
    }

    @Override
    public User insertUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public String timeout() {
        return null;
    }
}
