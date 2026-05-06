package com.project.ecommerce.controller;

import com.project.ecommerce.dto.StatusResponseDTO;
import com.project.ecommerce.service.StatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    private  final StatusService statusService;

    public HomeController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/hello")
    public String hello() {
        return statusService.getHello();
    }

    @GetMapping("/status")
    public StatusResponseDTO status(){
        return statusService.getStatus();

    }

}
