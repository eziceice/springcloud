package com.springboot.mall.controller;

import com.springboot.mall.pojo.User;
import com.springboot.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/ribbon")
    public User testRibbon() {
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = restTemplate.getForObject("http://USER/users/" + (i + 1), User.class);
        }
        return user;
    }

    @GetMapping("/feign")
    public User testFeign() {
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = userService.getUser((long) i);
        }
        return user;
    }
}
