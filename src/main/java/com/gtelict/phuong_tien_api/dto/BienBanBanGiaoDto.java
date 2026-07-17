package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BienBanBanGiaoDto {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(example = "BB-2024-001")
    private String soBienBan;
    
    @Schema(example = "2024-05-15")
    private LocalDate ngayBanGiao;
    
    @Schema(example = "DON_VI", description = "Loại bên nhận", allowableValues = {"CAN_BO", "DON_VI"})
    private String loaiBenNhan;
    
    @Schema(example = "DV001")
    private String idBenNhan;
    
    @Schema(example = "KHO", description = "Loại bên giao", allowableValues = {"CAN_BO", "DON_VI", "KHO"})
    private String loaiBenGiao;
    
    @Schema(example = "KHO_TONG")
    private String idBenGiao;
    
    @Schema(example = "CB002")
    private String nguoiKyId;
    
    private String fileDinhKem;
    
    private List<ChiTietBienBanDto> chiTietTaiSan;
}
