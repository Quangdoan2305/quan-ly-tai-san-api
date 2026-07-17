package com.gtelict.phuong_tien_api.dto;

import com.gtelict.phuong_tien_api.entity.CongDan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CanBoChiTiet extends CanBoTomTat {
    @Schema(description = "Thông tin chi tiết của công dân")
    private CongDan thongTinCongDan;
    
    @Schema(description = "Danh sách tài sản được cấp phát")
    private List<TaiSanRutGonDto> danhSachTaiSan;
}
