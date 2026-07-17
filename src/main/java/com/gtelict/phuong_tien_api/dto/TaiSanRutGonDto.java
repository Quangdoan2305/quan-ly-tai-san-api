package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
public class TaiSanRutGonDto {
    @Schema(example = "TS-001")
    private String maTaiSan;
    
    @Schema(example = "Màn hình Dell 27 inch")
    private String tenTaiSan;
    
    @Schema(example = "{\"doPhanGiai\": \"4K\", \"hang\": \"Dell\", \"inch\": 27}")
    private Map<String, Object> thongTinChiTiet;

    @Schema(example = "CB002")
    private String maNguoiCapPhat;

    @Schema(example = "Trần Văn B")
    private String nguoiCapPhat;

    @Schema(example = "2024-05-15")
    private java.time.LocalDate ngayCapPhat;
}
