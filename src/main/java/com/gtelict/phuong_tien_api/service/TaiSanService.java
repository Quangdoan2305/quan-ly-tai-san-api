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
        List<TaiSan> taiSans = taiSanRepo.findAllById(request.getDanhSachTaiSanId());
        for (TaiSan ts : taiSans) {
            // Update ownership
            if ("DON_VI".equals(request.getLoaiBenNhan())) {
                ts.setMaDonViQuanLy(request.getIdBenNhan());
                ts.setMaCanBoSuDung(null); // Giao cho đơn vị thì cá nhân không còn cầm
            } else if ("CAN_BO".equals(request.getLoaiBenNhan())) {
                ts.setMaCanBoSuDung(request.getIdBenNhan());
                // Không sửa maDonViQuanLy vì tài sản vẫn thuộc kho của đơn vị đó
            }
            
            ts.setMaNguoiCapPhat(request.getNguoiKyId());
            ts.setNgayCapPhat(LocalDate.now());
            
            ChiTietBienBan ct = new ChiTietBienBan();
            ct.setId(UUID.randomUUID().toString());
            ct.setBienBanId(bb.getId());
            ct.setTaiSanId(ts.getId());
            ct.setTinhTrangLucGiao(ts.getTinhTrang());
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
        
        dto.setDanhSachTaiSan(taiSans.stream().map(ts -> {
            TaiSanRutGonDto rg = new TaiSanRutGonDto();
            rg.setMaTaiSan(ts.getMaTaiSan());
            rg.setTenTaiSan(ts.getTenTaiSan());
            rg.setThongTinChiTiet(ts.getThongTinChiTiet());
            rg.setMaNguoiCapPhat(ts.getMaNguoiCapPhat());
            rg.setNgayCapPhat(ts.getNgayCapPhat());
            if (ts.getMaNguoiCapPhat() != null) rg.setNguoiCapPhat("Tên " + ts.getMaNguoiCapPhat());
            return rg;
        }).collect(Collectors.toList()));
        
        return dto;
    }

    private TaiSanDto mapToDto(TaiSan ts) {
        TaiSanDto dto = new TaiSanDto();
        dto.setId(ts.getId());
        dto.setMaTaiSan(ts.getMaTaiSan());
        dto.setTenTaiSan(ts.getTenTaiSan());
        dto.setMaDanhMucLoai(ts.getMaDanhMucLoai());
        if (ts.getMaDanhMucLoai() != null) dto.setTenLoaiTaiSan("Tên loại " + ts.getMaDanhMucLoai());
        dto.setThongTinChiTiet(ts.getThongTinChiTiet());
        dto.setTinhTrang(ts.getTinhTrang());
        
        dto.setMaDonViQuanLy(ts.getMaDonViQuanLy());
        if (ts.getMaDonViQuanLy() != null) dto.setTenDonViQuanLy("Tên đơn vị " + ts.getMaDonViQuanLy());
        
        dto.setMaCanBoSuDung(ts.getMaCanBoSuDung());
        if (ts.getMaCanBoSuDung() != null) dto.setTenCanBoSuDung("Tên cán bộ " + ts.getMaCanBoSuDung());
        
        dto.setMaNguoiCapPhat(ts.getMaNguoiCapPhat());
        if (ts.getMaNguoiCapPhat() != null) dto.setNguoiCapPhat("Tên người cấp phát " + ts.getMaNguoiCapPhat());
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
            return dto;
        }).collect(Collectors.toList()));
        
        return response;
    }

    public PageResponse<BienBanBanGiaoDto> searchBienBan(String soBienBan, LocalDate tuNgay, LocalDate denNgay, String loaiBenNhan, String idBenNhan, String loaiBenGiao, String idBenGiao, String nguoiKyId, String maDonVi, Pageable pageable) {
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
            return dto;
        }).collect(Collectors.toList()));
        
        return response;
    }
}
