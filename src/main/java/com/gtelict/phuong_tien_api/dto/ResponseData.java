package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResponseData<T> {
    @Schema(example = "SUCCESS")
    private String code = "SUCCESS";
    
    @Schema(example = "Thành công")
    private String message = "Thành công";
    
    private T data;
    
    public ResponseData(T data) { this.data = data; }
}