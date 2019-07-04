package com.springboot.mall.service;

import com.springboot.mall.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user")
public interface UserService {
    @GetMapping("/users/{id}")
    User getUser(@PathVariable("id") Long id);
}
