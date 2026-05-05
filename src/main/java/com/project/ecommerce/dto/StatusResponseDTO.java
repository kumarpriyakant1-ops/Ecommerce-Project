package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
public class StatusResponseDTO {
    private  String status;
    private  String service;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  String time;

    public StatusResponseDTO(String status, String service, String time) {
        this.status = status;
        this.service = service;
        this.time = time;
    }

}
