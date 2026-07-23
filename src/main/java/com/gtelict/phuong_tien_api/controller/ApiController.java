package com.gtelict.phuong_tien_api.controller;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.*;
import com.gtelict.phuong_tien_api.repository.*;
import com.gtelict.phuong_tien_api.service.CanBoChienSiService;
import com.gtelict.phuong_tien_api.service.TaiSanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
public class ApiController {

    @Autowired
    private CanBoChienSiService canBoService;
    @Autowired
    private TaiSanService taiSanService;
    
    @Autowired
    private DonViRepository donViRepo;
    @Autowired
    private DiaGioiHanhChinhRepository diaGioiRepo;
    @Autowired
    private DanTocRepository danTocRepo;
    @Autowired
    private TonGiaoRepository tonGiaoRepo;
    @Autowired
    private LoaiTaiSanRepository loaiTaiSanRepo;

    @Tag(name = "1. Cán bộ chiến sĩ")
    @Operation(summary = "Lấy danh sách cán bộ chiến sĩ", description = "API tra cứu danh sách cán bộ chiến sĩ, phục vụ việc chia sẻ khai thác thông tin (có hỗ trợ phân trang).")
    @GetMapping("/can-bo")
    public ResponseData<PageResponse<CanBoTomTat>> getCanBo(@RequestParam(required = false) String maDonVi,
            @RequestParam(required = false) String maCanBo,
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String soCccd,
            @RequestParam(required = false) String capBac,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(
                canBoService.search(maDonVi, maCanBo, capBac, hoTen, soCccd, PageRequest.of(page, size)));
    }

    @Tag(name = "1. Cán bộ chiến sĩ")
    @Operation(summary = "Lấy chi tiết cán bộ", description = "Lấy thông tin chi tiết của một cán bộ chiến sĩ, bao gồm thông tin tài sản được giao.")
    @GetMapping("/can-bo/{id}")
    public ResponseData<CanBoChiTiet> getCanBoById(@PathVariable String id, jakarta.servlet.http.HttpServletRequest request) {
        return new ResponseData<>(canBoService.getChiTiet(id, request));
    }
    
    @Tag(name = "2. Tài sản & Trang thiết bị")
    @Operation(summary = "Lấy danh sách tài sản", description = "API tra cứu danh sách tài sản (có hỗ trợ phân trang). Cho biết đơn vị nào được cấp, cá nhân nào đang sử dụng.")
    @GetMapping("/tai-san")
    public ResponseData<PageResponse<TaiSanDto>> getTaiSan(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) String maDonViQuanLy,
            @RequestParam(required = false) String maCanBoSuDung,
            @RequestParam(required = false) String tinhTrang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<TaiSanDto> result = taiSanService.search(tuKhoa, maDonViQuanLy, maCanBoSuDung, tinhTrang, pageable);
        return new ResponseData<>(result);
    }

    @Tag(name = "2. Tài sản & Trang thiết bị")
    @Operation(summary = "Lấy chi tiết tài sản", description = "API lấy thông tin chi tiết của một tài sản, bao gồm cấu hình kỹ thuật động (JSON).")
    @GetMapping("/tai-san/{id}")
    public ResponseData<TaiSanDto> getTaiSanById(@PathVariable String id) {
        TaiSanDto ts = taiSanService.getChiTiet(id);
        return new ResponseData<>(ts);
    }

    @Tag(name = "2. Tài sản & Trang thiết bị")
    @Operation(summary = "Xem lịch sử bàn giao của tài sản", description = "Lấy danh sách các biên bản bàn giao liên quan đến một tài sản cụ thể (có phân trang).")
    @GetMapping("/tai-san/{id}/lich-su-ban-giao")
    public ResponseData<PageResponse<BienBanBanGiaoDto>> getLichSuBanGiao(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(taiSanService.getLichSuBanGiao(id, PageRequest.of(page, size)));
    }

    @Tag(name = "3. Bàn giao tài sản")
    @Operation(summary = "Lập biên bản bàn giao tài sản", description = "Ghi nhận việc luân chuyển tài sản giữa kho tổng, đơn vị, và cá nhân.")

    @PostMapping("/ban-giao")
    public ResponseData<BienBanBanGiaoDto> banGiaoTaiSan(@Valid @RequestBody BienBanRequest request) {
        return new ResponseData<>(taiSanService.banGiaoTaiSan(request));
    }

    @Tag(name = "3. Bàn giao tài sản")
    @Operation(summary = "Xem chi tiết biên bản", description = "Lấy toàn bộ thông tin chi tiết biên bản và danh sách tài sản kèm theo.")
    @GetMapping("/ban-giao/{id}")
    public ResponseData<BienBanBanGiaoDto> getChiTietBienBan(@PathVariable String id) {
        BienBanBanGiaoDto dto = taiSanService.getChiTietBienBan(id);
        return new ResponseData<>(dto);
    }

    @Tag(name = "3. Bàn giao tài sản")
    @Operation(summary = "Phê duyệt biên bản", description = "Phê duyệt biên bản đang ở trạng thái NHAP, chốt quyền sở hữu tài sản.")
    @PutMapping("/ban-giao/{id}/ky")
    public ResponseData<BienBanBanGiaoDto> pheDuyetBienBan(@PathVariable String id, @Valid @RequestBody PheDuyetRequest request) {
        return new ResponseData<>(taiSanService.pheDuyetBienBan(id, request));
    }

    @Tag(name = "3. Bàn giao tài sản")
    @Operation(summary = "Tra cứu danh sách biên bản bàn giao", description = "API tìm kiếm và lọc danh sách các biên bản bàn giao (phục vụ nghiệp vụ văn thư).")
    @GetMapping("/ban-giao")
    public ResponseData<PageResponse<BienBanBanGiaoDto>> searchBienBan(
            @RequestParam(required = false) String soBienBan,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate denNgay,
            @RequestParam(required = false) String loaiBenNhan,
            @RequestParam(required = false) String idBenNhan,
            @RequestParam(required = false) String loaiBenGiao,
            @RequestParam(required = false) String idBenGiao,
            @RequestParam(required = false) String nguoiKyId,
            @RequestParam(required = false) String maDonVi,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(taiSanService.searchBienBan(soBienBan, tuNgay, denNgay, loaiBenNhan, idBenNhan,
                loaiBenGiao, idBenGiao, nguoiKyId, maDonVi, trangThai, PageRequest.of(page, size)));
    }

    // ==========================================
    // 3. DANH MỤC API (TÁCH 5 ENDPOINTS)
    // ==========================================

    @Tag(name = "4. Danh mục")
    @Operation(summary = "Lấy dữ liệu danh mục Đơn vị", description = "Lọc theo parentId và updated_after (Delta Sync). Có phân trang.")
    @GetMapping("/danh-muc/don-vi")
    public ResponseData<PageResponse<DonViDto>> getDonVi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updated_after,
            @RequestParam(required = false) String parentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        Page<DonVi> entityPage = donViRepo.search(updated_after, parentId, PageRequest.of(page, size));
        
        PageResponse<DonViDto> response = new PageResponse<>();
        response.setPageSize(entityPage.getSize());
        response.setPageNumber(entityPage.getNumber());
        response.setTotalPages(entityPage.getTotalPages());
        response.setTotalElements(entityPage.getTotalElements());
        response.setContent(entityPage.getContent().stream().map(e -> {
            DonViDto dto = new DonViDto();
            dto.setId(e.getId());
            dto.setMa(e.getMa());
            dto.setTen(e.getTen());
            if (e.getParent() != null) dto.setParentId(e.getParent().getId());
            dto.setDiaChi(e.getDiaChi());
            dto.setSoDienThoai(e.getSoDienThoai());
            dto.setTrangThai(e.getTrangThai());
            dto.setNgayCapNhat(e.getNgayCapNhat());
            return dto;
        }).collect(Collectors.toList()));
        
        return new ResponseData<>(response);
    }

    @Tag(name = "4. Danh mục")
    @Operation(summary = "Lấy dữ liệu danh mục Địa giới hành chính", description = "Lọc theo cap, parentId và updated_after. Có phân trang.")
    @GetMapping("/danh-muc/dia-gioi")
    public ResponseData<PageResponse<DiaGioiHanhChinhDto>> getDiaGioi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updated_after,
            @RequestParam(required = false) Integer cap,
            @RequestParam(required = false) String parentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size) {
        
        Page<DiaGioiHanhChinh> entityPage = diaGioiRepo.search(updated_after, cap, parentId, PageRequest.of(page, size));
        
        PageResponse<DiaGioiHanhChinhDto> response = new PageResponse<>();
        response.setPageSize(entityPage.getSize());
        response.setPageNumber(entityPage.getNumber());
        response.setTotalPages(entityPage.getTotalPages());
        response.setTotalElements(entityPage.getTotalElements());
        response.setContent(entityPage.getContent().stream().map(e -> {
            DiaGioiHanhChinhDto dto = new DiaGioiHanhChinhDto();
            dto.setId(e.getId());
            dto.setMa(e.getMa());
            dto.setTen(e.getTen());
            dto.setCap(e.getCap());
            if (e.getParent() != null) dto.setParentId(e.getParent().getId());
            dto.setTrangThai(e.getTrangThai());
            dto.setNgayCapNhat(e.getNgayCapNhat());
            return dto;
        }).collect(Collectors.toList()));
        
        return new ResponseData<>(response);
    }

    @Tag(name = "4. Danh mục")
    @Operation(summary = "Lấy dữ liệu danh mục Dân tộc", description = "Lọc theo updated_after (Delta Sync). Không phân trang.")
    @GetMapping("/danh-muc/dan-toc")
    public ResponseData<List<DanhMucCoBanDto>> getDanToc(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updated_after) {
        
        List<DanToc> entities = danTocRepo.search(updated_after);
        List<DanhMucCoBanDto> dtos = entities.stream().map(e -> {
            DanhMucCoBanDto dto = new DanhMucCoBanDto();
            dto.setId(e.getId());
            dto.setMa(e.getMa());
            dto.setTen(e.getTen());
            dto.setTrangThai(e.getTrangThai());
            dto.setNgayCapNhat(e.getNgayCapNhat());
            return dto;
        }).collect(Collectors.toList());
        return new ResponseData<>(dtos);
    }

    @Tag(name = "4. Danh mục")
    @Operation(summary = "Lấy dữ liệu danh mục Tôn giáo", description = "Lọc theo updated_after (Delta Sync). Không phân trang.")
    @GetMapping("/danh-muc/ton-giao")
    public ResponseData<List<DanhMucCoBanDto>> getTonGiao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updated_after) {
        
        List<TonGiao> entities = tonGiaoRepo.search(updated_after);
        List<DanhMucCoBanDto> dtos = entities.stream().map(e -> {
            DanhMucCoBanDto dto = new DanhMucCoBanDto();
            dto.setId(e.getId());
            dto.setMa(e.getMa());
            dto.setTen(e.getTen());
            dto.setTrangThai(e.getTrangThai());
            dto.setNgayCapNhat(e.getNgayCapNhat());
            return dto;
        }).collect(Collectors.toList());
        return new ResponseData<>(dtos);
    }

    @Tag(name = "4. Danh mục")
    @Operation(summary = "Lấy dữ liệu danh mục Loại tài sản", description = "Lọc theo updated_after (Delta Sync). Có phân trang.")
    @GetMapping("/danh-muc/loai-tai-san")
    public ResponseData<List<DanhMucCoBanDto>> getLoaiTaiSan(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updated_after) {
        
        Pageable pageable = PageRequest.of(0, 1000);
        Page<LoaiTaiSan> page = loaiTaiSanRepo.findAllWithDeltaSync(updated_after, pageable);
        List<LoaiTaiSan> entities = page.getContent();
        List<DanhMucCoBanDto> dtos = entities.stream().map(e -> {
            DanhMucCoBanDto dto = new DanhMucCoBanDto();
            dto.setId(e.getId());
            dto.setMa(e.getMa());
            dto.setTen(e.getTen());
            dto.setTrangThai(e.getTrangThai());
            dto.setNgayCapNhat(e.getNgayCapNhat());
            return dto;
        }).collect(Collectors.toList());
        return new ResponseData<>(dtos);
    }
}