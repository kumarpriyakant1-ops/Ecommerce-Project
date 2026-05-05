package com.project.ecommerce.controlller;

import com.project.ecommerce.dto.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Backend is running 🚀";
    }

    @GetMapping("/status")
    public StatusResponse status(){
        return new StatusResponse("UP", "E-commerce backend");

    }

}
