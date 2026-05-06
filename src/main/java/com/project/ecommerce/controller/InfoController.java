package com.project.ecommerce.controller;

import com.project.ecommerce.dto.InfoDTO;
import com.project.ecommerce.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InfoController {


    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/info")
    public InfoDTO info(){
        return infoService.getInfo();

    }

}
