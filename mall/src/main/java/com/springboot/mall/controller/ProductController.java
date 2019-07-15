package com.springboot.mall.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springboot.mall.pojo.User;
import com.springboot.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

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

    @PostMapping("/feign2")
    public User testFeignPost() {
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = userService.insertUser(new User());
        }
        return user;
    }

    @PutMapping("/feign3")
    public User testFeignPut() {
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = userService.updateUser(new User());
        }
        return user;
    }

    @GetMapping("/hystrix")
    @HystrixCommand(fallbackMethod = "error", commandProperties =
            {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")})
    public String circuitBreaker() {
        return userService.timeout();
    }

    public String error() {
        return "Timeout!";
    }

    @GetMapping("/loadBalancer")
    public String testLoadBalancerClient() {
        ServiceInstance instance = loadBalancerClient.choose("USER");
        return instance.getHost() + ":" + instance.getPort();
    }

    /**
     * Ribbon manage the registry service lists itself, not from Eureka
     *
     * @return
     */
    @GetMapping("/testWithoutEureka")
    public String testWithoutEureka() {
        ServiceInstance instance = loadBalancerClient.choose("users");
        return instance.getHost() + ":" + instance.getPort();
    }
}
