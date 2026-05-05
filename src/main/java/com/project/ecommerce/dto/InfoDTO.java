package com.project.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InfoDTO {
    private String app;
    private String version;
    private String Developer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;


    public InfoDTO(String app, String version, String developer, LocalDateTime time) {
        this.app = app;
        this.version = version;
        Developer = developer;
        this.time = time;
    }

}
