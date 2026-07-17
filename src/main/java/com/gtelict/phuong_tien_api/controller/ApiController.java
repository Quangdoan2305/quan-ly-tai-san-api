package com.gtelict.phuong_tien_api.controller;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.DanhMuc;
import com.gtelict.phuong_tien_api.repository.DanhMucRepository;
import com.gtelict.phuong_tien_api.service.CanBoChienSiService;
import com.gtelict.phuong_tien_api.service.TaiSanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ApiController {
    
    @Autowired private CanBoChienSiService canBoService;
    @Autowired private TaiSanService taiSanService;
    @Autowired private DanhMucRepository danhMucRepo;

    @Tag(name = "1. Dịch vụ khai thác dữ liệu thông tin cán bộ chiến sĩ qua LGSP")
    @Operation(summary = "Lấy danh sách cán bộ chiến sĩ", description = "API tra cứu danh sách cán bộ chiến sĩ, phục vụ việc chia sẻ khai thác thông tin (có hỗ trợ phân trang).")
    @GetMapping("/can-bo")
    public ResponseData<PageResponse<CanBoTomTat>> getCanBo(@RequestParam(required = false) String maDonVi,
                                                            @RequestParam(required = false) String maCanBo,
                                                            @RequestParam(required = false) String hoTen,
                                                            @RequestParam(required = false) String soCccd,
                                                            @RequestParam(required = false) String capBac,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(canBoService.search(maDonVi, maCanBo, capBac, hoTen, soCccd, PageRequest.of(page, size)));
    }

    @Tag(name = "1. Dịch vụ khai thác dữ liệu thông tin cán bộ chiến sĩ qua LGSP")
    @Operation(summary = "Lấy thông tin chi tiết một cán bộ chiến sĩ", description = "API lấy thông tin chi tiết của một cán bộ chiến sĩ, bao gồm thông tin tài sản được cấp phát.")
    @GetMapping("/can-bo/{id}")
    public ResponseData<CanBoChiTiet> getCanBoById(@PathVariable String id) {
        return new ResponseData<>(canBoService.getChiTiet(id));
    }

    @Tag(name = "2. Dịch vụ quản lý tài sản & trang thiết bị")
    @Operation(summary = "Lấy danh sách tài sản", description = "API tra cứu danh sách tài sản (có hỗ trợ phân trang). Cho biết đơn vị nào được cấp, cá nhân nào đang sử dụng.")
    @GetMapping("/tai-san")
    public ResponseData<PageResponse<TaiSanDto>> getTaiSan(@RequestParam(required = false) String maDonViQuanLy,
                                                           @RequestParam(required = false) String maCanBoSuDung,
                                                           @RequestParam(required = false) String tinhTrang,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(taiSanService.search(maDonViQuanLy, maCanBoSuDung, tinhTrang, PageRequest.of(page, size)));
    }

    @Tag(name = "2. Dịch vụ quản lý tài sản & trang thiết bị")
    @Operation(summary = "Lấy chi tiết tài sản", description = "API lấy thông tin chi tiết của một tài sản, bao gồm cấu hình kỹ thuật động (JSON).")
    @GetMapping("/tai-san/{id}")
    public ResponseData<TaiSanDto> getTaiSanById(@PathVariable String id) {
        TaiSanDto ts = taiSanService.getChiTiet(id);
        if (ts == null) {
            return new ResponseData<TaiSanDto>(null);
        }
        return new ResponseData<>(ts);
    }

    @GetMapping("/tai-san/{id}/lich-su-ban-giao")
    public ResponseData<PageResponse<BienBanBanGiaoDto>> getLichSuBanGiao(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(taiSanService.getLichSuBanGiao(id, PageRequest.of(page, size)));
    }

    @Tag(name = "2. Dịch vụ quản lý tài sản & trang thiết bị")
    @Operation(summary = "Lập biên bản bàn giao tài sản", description = "Ghi nhận việc luân chuyển tài sản giữa kho tổng, đơn vị, và cá nhân.")
    @PostMapping("/ban-giao")
    public ResponseData<BienBanBanGiaoDto> banGiaoTaiSan(@RequestBody BienBanRequest request) {
        return new ResponseData<>(taiSanService.banGiaoTaiSan(request));
    }

    @Tag(name = "2. Dịch vụ khai thác dữ liệu thông tin tài sản qua LGSP")
    @Operation(summary = "Tra cứu danh sách biên bản bàn giao", description = "API tìm kiếm và lọc danh sách các biên bản bàn giao (phục vụ nghiệp vụ văn thư).")
    @GetMapping("/ban-giao")
    public ResponseData<PageResponse<BienBanBanGiaoDto>> searchBienBan(
            @RequestParam(required = false) String soBienBan,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate tuNgay,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate denNgay,
            @RequestParam(required = false) String loaiBenNhan,
            @RequestParam(required = false) String idBenNhan,
            @RequestParam(required = false) String loaiBenGiao,
            @RequestParam(required = false) String idBenGiao,
            @RequestParam(required = false) String nguoiKyId,
            @RequestParam(required = false) String maDonVi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseData<>(taiSanService.searchBienBan(soBienBan, tuNgay, denNgay, loaiBenNhan, idBenNhan, loaiBenGiao, idBenGiao, nguoiKyId, maDonVi, PageRequest.of(page, size)));
    }

    @Tag(name = "3. Dịch vụ chia sẻ dữ liệu danh mục qua LGSP")
    @Operation(summary = "Lấy dữ liệu danh mục", description = "API chia sẻ dữ liệu danh mục (địa giới hành chính, dân tộc, tôn giáo, đơn vị, loại tài sản) (có hỗ trợ phân trang).")
    @GetMapping("/danh-muc/{loai}")
    public ResponseData<PageResponse<DanhMuc>> getDanhMuc(@PathVariable String loai,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<DanhMuc> danhMucPage = danhMucRepo.findByLoai(loai, PageRequest.of(page, size));
        return new ResponseData<>(new PageResponse<>(danhMucPage));
    }
}