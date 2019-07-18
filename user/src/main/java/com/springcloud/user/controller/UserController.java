package com.springcloud.user.controller;

import com.springcloud.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        ServiceInstance serviceInstance = discoveryClient.getInstances("USER").get(0);
        System.out.println("[" + serviceInstance.getServiceId() + "]"
                + serviceInstance.getHost() + ":" + serviceInstance.getPort());
        return User.builder().id(id).level(1).userName("username_" + id).note("note_" + id).build();
    }

    @GetMapping("/hi")
    public String hi() {
        return "I'm a user";
    }
}
