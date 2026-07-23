package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChiTietBienBanRequestDto {
    @NotBlank(message = "ID tài sản không được để trống")
    @Schema(example = "TS-01")
    private String taiSanId;
    
    @Schema(example = "Mới 100%")
    private String tinhTrangLucGiao;
    
    @Schema(example = "Giao từ Phòng cho đ/c Bình")
    private String ghiChu;
}
