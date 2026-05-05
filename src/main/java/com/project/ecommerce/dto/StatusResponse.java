package com.project.ecommerce.dto;

import lombok.Getter;

@Getter
public class StatusResponse {
    private  String status;
    private  String service;

    public StatusResponse(String status, String service) {
        this.status = status;
        this.service = service;
    }

}
