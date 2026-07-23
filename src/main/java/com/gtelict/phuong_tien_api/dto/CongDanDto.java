package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CongDanDto {
    private String id;
    private String soCccd;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String queQuan;
    
    @Schema(description = "Tên dân tộc")
    private String tenDanToc;
    
    @Schema(description = "Tên tôn giáo")
    private String tenTonGiao;
    
    @Schema(description = "Tên địa giới hành chính thường trú")
    private String tenDghcThuongTru;
    
    private String diaChiThuongTru;
    
    @Schema(description = "Tên địa giới hành chính hiện tại")
    private String tenDghcHienTai;
    
    private String diaChiHienTai;
}
