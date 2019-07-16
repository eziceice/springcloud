package com.springboot.mall.service;

import com.springboot.mall.config.FeignConfig;
import com.springboot.mall.pojo.User;
import com.springboot.mall.service.hystrix.UserServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "user", configuration = FeignConfig.class, fallback = UserServiceHystrix.class)
public interface UserService {

    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") Long id);

    @PostMapping("/users")
    User insertUser(@RequestBody(required = false) User user);

    @PutMapping("/users")
    User updateUser(@RequestBody(required = false) User user);

    @GetMapping("/users/timeout")
    String timeout();
}
