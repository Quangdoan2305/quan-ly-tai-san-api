package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CanBoTomTat {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(example = "CB001")
    private String maCanBo;
    
    @Schema(example = "Nguyễn Văn A")
    private String hoTen;
    
    @Schema(example = "1990-01-01")
    private LocalDate ngaySinh;
    
    @Schema(example = "Nam")
    private String gioiTinh;
    
    @Schema(example = "Đại úy")
    private String capBac;
    
    @Schema(example = "Trưởng phòng")
    private String chucVu;
    
    @Schema(example = "DV001")
    private String maDonVi;
    
    @Schema(example = "Phòng Cảnh sát giao thông")
    private String tenDonVi;
    
    @Schema(example = "CD-123456")
    private String maCongDan;
}
