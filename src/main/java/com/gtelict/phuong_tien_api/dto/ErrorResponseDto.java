package com.gtelict.phuong_tien_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String title;
    private String errorCode;
    private String message;
    private String uri;
    private LocalDateTime time;
    private String requestId;
}
