package com.project.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserTestController {

    @GetMapping("/test")
    public String userTest(){

        return "Welcome User";
    }

}
