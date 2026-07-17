package com.gtelict.phuong_tien_api.service;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.*;
import com.gtelict.phuong_tien_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaiSanService {
    @Autowired private TaiSanRepository taiSanRepo;
    @Autowired private BienBanBanGiaoRepository bienBanRepo;
    @Autowired private ChiTietBienBanRepository chiTietRepo;

    public PageResponse<TaiSanDto> search(String maDonViQuanLy, String maCanBoSuDung, String tinhTrang, Pageable pageable) {
        Page<TaiSan> page = taiSanRepo.search(maDonViQuanLy, maCanBoSuDung, tinhTrang, pageable);
        
        PageResponse<TaiSanDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setContent(page.getContent().stream().map(this::mapToDto).collect(Collectors.toList()));
        return response;
    }

    public TaiSanDto getChiTiet(String id) {
        TaiSan ts = taiSanRepo.findById(id).orElse(null);
        return ts != null ? mapToDto(ts) : null;
    }

    @Transactional
    public BienBanBanGiaoDto banGiaoTaiSan(BienBanRequest request) {
        // 1. Tạo Biên bản
        BienBanBanGiao bb = new BienBanBanGiao();
        bb.setId(UUID.randomUUID().toString());
        bb.setSoBienBan(request.getSoBienBan());
        bb.setNgayBanGiao(LocalDate.now());
        bb.setLoaiBenNhan(request.getLoaiBenNhan());
        bb.setIdBenNhan(request.getIdBenNhan());
        bb.setLoaiBenGiao(request.getLoaiBenGiao());
        bb.setIdBenGiao(request.getIdBenGiao());
        bb.setNguoiKyId(request.getNguoiKyId());
        bienBanRepo.save(bb);

        // 2. Xử lý từng tài sản & Lưu chi tiết biên bản
        List<String> taiSanIds = request.getChiTietTaiSan().stream().map(ChiTietBienBanRequestDto::getTaiSanId).collect(Collectors.toList());
        List<TaiSan> taiSans = taiSanRepo.findAllById(taiSanIds);
        
        for (ChiTietBienBanRequestDto ctReq : request.getChiTietTaiSan()) {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ctReq.getTaiSanId())).findFirst().orElse(null);
            if (ts == null) continue;
            
            // Update ownership
            if ("DON_VI".equals(request.getLoaiBenNhan())) {
                ts.setIdDonViQuanLy(request.getIdBenNhan());
                ts.setIdCanBoSuDung(null); // Giao cho đơn vị thì cá nhân không còn cầm
            } else if ("CAN_BO".equals(request.getLoaiBenNhan())) {
                ts.setIdCanBoSuDung(request.getIdBenNhan());
                // Không sửa idDonViQuanLy vì tài sản vẫn thuộc kho của đơn vị đó
            }
            
            ts.setIdNguoiCapPhat(request.getNguoiKyId());
            ts.setNgayCapPhat(LocalDate.now());
            
            ChiTietBienBan ct = new ChiTietBienBan();
            ct.setId(UUID.randomUUID().toString());
            ct.setBienBanId(bb.getId());
            ct.setTaiSanId(ts.getId());
            ct.setTinhTrangLucGiao(ctReq.getTinhTrangLucGiao());
            ct.setGhiChu(ctReq.getGhiChu());
            chiTietRepo.save(ct);
        }
        taiSanRepo.saveAll(taiSans);

        // 3. Trả về DTO
        BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
        dto.setId(bb.getId());
        dto.setSoBienBan(bb.getSoBienBan());
        dto.setNgayBanGiao(bb.getNgayBanGiao());
        dto.setLoaiBenNhan(bb.getLoaiBenNhan());
        dto.setIdBenNhan(bb.getIdBenNhan());
        dto.setLoaiBenGiao(bb.getLoaiBenGiao());
        dto.setIdBenGiao(bb.getIdBenGiao());
        dto.setNguoiKyId(bb.getNguoiKyId());
        dto.setFileDinhKem(bb.getFileDinhKem());
        
        dto.setChiTietTaiSan(request.getChiTietTaiSan().stream().map(ctReq -> {
            TaiSan ts = taiSans.stream().filter(t -> t.getId().equals(ctReq.getTaiSanId())).findFirst().orElse(null);
            ChiTietBienBanDto ctDto = new ChiTietBienBanDto();
            ctDto.setTinhTrangLucGiao(ctReq.getTinhTrangLucGiao());
            ctDto.setGhiChu(ctReq.getGhiChu());
            if (ts != null) {
                TaiSanRutGonDto rg = new TaiSanRutGonDto();
                rg.setMaTaiSan(ts.getMaTaiSan());
                rg.setTenTaiSan(ts.getTenTaiSan());
                rg.setThongTinChiTiet(ts.getThongTinChiTiet());
                rg.setMaNguoiCapPhat(ts.getIdNguoiCapPhat());
                rg.setNgayCapPhat(ts.getNgayCapPhat());
                if (ts.getIdNguoiCapPhat() != null) rg.setNguoiCapPhat("Tên " + ts.getIdNguoiCapPhat());
                ctDto.setTaiSan(rg);
            }
            return ctDto;
        }).collect(Collectors.toList()));
        
        return dto;
    }

    private TaiSanDto mapToDto(TaiSan ts) {
        TaiSanDto dto = new TaiSanDto();
        dto.setId(ts.getId());
        dto.setMaTaiSan(ts.getMaTaiSan());
        dto.setTenTaiSan(ts.getTenTaiSan());
        dto.setMaDanhMucLoai(ts.getIdDanhMucLoai());
        if (ts.getIdDanhMucLoai() != null) dto.setTenLoaiTaiSan("Tên loại " + ts.getIdDanhMucLoai());
        dto.setThongTinChiTiet(ts.getThongTinChiTiet());
        dto.setTinhTrang(ts.getTinhTrang());
        
        dto.setMaDonViQuanLy(ts.getIdDonViQuanLy());
        if (ts.getIdDonViQuanLy() != null) dto.setTenDonViQuanLy("Tên đơn vị " + ts.getIdDonViQuanLy());
        
        dto.setMaCanBoSuDung(ts.getIdCanBoSuDung());
        if (ts.getIdCanBoSuDung() != null) dto.setTenCanBoSuDung("Tên cán bộ " + ts.getIdCanBoSuDung());
        
        dto.setMaNguoiCapPhat(ts.getIdNguoiCapPhat());
        if (ts.getIdNguoiCapPhat() != null) dto.setNguoiCapPhat("Tên người cấp phát " + ts.getIdNguoiCapPhat());
        dto.setNgayCapPhat(ts.getNgayCapPhat());
        
        return dto;
    }

    public PageResponse<BienBanBanGiaoDto> getLichSuBanGiao(String taiSanId, Pageable pageable) {
        Page<BienBanBanGiao> page = bienBanRepo.findByTaiSanId(taiSanId, pageable);
        
        PageResponse<BienBanBanGiaoDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        
        response.setContent(page.getContent().stream().map(bb -> {
            BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
            dto.setId(bb.getId());
            dto.setSoBienBan(bb.getSoBienBan());
            dto.setNgayBanGiao(bb.getNgayBanGiao());
            dto.setLoaiBenNhan(bb.getLoaiBenNhan());
            dto.setIdBenNhan(bb.getIdBenNhan());
            dto.setLoaiBenGiao(bb.getLoaiBenGiao());
            dto.setIdBenGiao(bb.getIdBenGiao());
            dto.setNguoiKyId(bb.getNguoiKyId());
            dto.setFileDinhKem(bb.getFileDinhKem());
            return dto;
        }).collect(Collectors.toList()));
        
        return response;
    }

    public PageResponse<BienBanBanGiaoDto> searchBienBan(String soBienBan, LocalDate tuNgay, LocalDate denNgay, String loaiBenNhan, String idBenNhan, String loaiBenGiao, String idBenGiao, String nguoiKyId, String maDonVi, Pageable pageable) {
        soBienBan = soBienBan == null ? "" : soBienBan;
        tuNgay = tuNgay == null ? LocalDate.of(1900, 1, 1) : tuNgay;
        denNgay = denNgay == null ? LocalDate.of(2100, 1, 1) : denNgay;
        loaiBenNhan = loaiBenNhan == null ? "" : loaiBenNhan;
        idBenNhan = idBenNhan == null ? "" : idBenNhan;
        loaiBenGiao = loaiBenGiao == null ? "" : loaiBenGiao;
        idBenGiao = idBenGiao == null ? "" : idBenGiao;
        nguoiKyId = nguoiKyId == null ? "" : nguoiKyId;
        maDonVi = maDonVi == null ? "" : maDonVi;

        Page<BienBanBanGiao> page = bienBanRepo.search(soBienBan, tuNgay, denNgay, loaiBenNhan, idBenNhan, loaiBenGiao, idBenGiao, nguoiKyId, maDonVi, pageable);
        
        PageResponse<BienBanBanGiaoDto> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        
        response.setContent(page.getContent().stream().map(bb -> {
            BienBanBanGiaoDto dto = new BienBanBanGiaoDto();
            dto.setId(bb.getId());
            dto.setSoBienBan(bb.getSoBienBan());
            dto.setNgayBanGiao(bb.getNgayBanGiao());
            dto.setLoaiBenNhan(bb.getLoaiBenNhan());
            dto.setIdBenNhan(bb.getIdBenNhan());
            dto.setLoaiBenGiao(bb.getLoaiBenGiao());
            dto.setIdBenGiao(bb.getIdBenGiao());
            dto.setNguoiKyId(bb.getNguoiKyId());
            dto.setFileDinhKem(bb.getFileDinhKem());
            return dto;
        }).collect(Collectors.toList()));
        
        return response;
    }
}
