package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class BienBanRequest {
    @Schema(example = "BB-2024-001")
    private String soBienBan;
    
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
    
    @Schema(description = "Danh sách ID tài sản bàn giao")
    private List<String> danhSachTaiSanId;
}
