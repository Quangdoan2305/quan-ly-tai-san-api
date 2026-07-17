package com.gtelict.phuong_tien_api.service;

import com.gtelict.phuong_tien_api.dto.*;
import com.gtelict.phuong_tien_api.entity.*;
import com.gtelict.phuong_tien_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CanBoChienSiService {
    @Autowired private CanBoChienSiRepository canBoRepo;
    @Autowired private TaiSanRepository taiSanRepo;
    @Autowired private DanhMucRepository danhMucRepo;

    public PageResponse<CanBoTomTat> search(String maDonVi, String maCanBo, String capBac, String hoTen, String soCccd, Pageable pageable) {
        Page<CanBoChienSi> page = canBoRepo.search(maDonVi, maCanBo, capBac, hoTen, soCccd, pageable);
        List<CanBoTomTat> dtoList = page.getContent().stream().map(this::mapToTomTat).collect(Collectors.toList());
        
        PageResponse<CanBoTomTat> response = new PageResponse<>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setContent(dtoList);
        return response;
    }

    public CanBoChiTiet getChiTiet(String id) {
        CanBoChienSi entity = canBoRepo.findById(id).orElse(null);
        if (entity == null) return null;
        
        CanBoChiTiet chiTiet = new CanBoChiTiet();
        chiTiet.setId(entity.getId());
        chiTiet.setMaCanBo(entity.getMaCanBo());
        chiTiet.setCapBac(entity.getCapBac());
        chiTiet.setChucVu(entity.getChucVu());
        chiTiet.setMaDonVi(entity.getMaDonVi());
        chiTiet.setTenDonVi(getTenDanhMuc(entity.getMaDonVi()));
        
        if (entity.getCongDan() != null) {
            chiTiet.setHoTen(entity.getCongDan().getHoTen());
            chiTiet.setNgaySinh(entity.getCongDan().getNgaySinh());
            chiTiet.setGioiTinh(entity.getCongDan().getGioiTinh());
            chiTiet.setMaCongDan(entity.getCongDan().getId());
        }
        chiTiet.setThongTinCongDan(entity.getCongDan());

        List<TaiSan> taiSans = taiSanRepo.findByMaCanBoSuDung(entity.getId());
        chiTiet.setDanhSachTaiSan(taiSans.stream().map(ts -> {
            TaiSanRutGonDto rg = new TaiSanRutGonDto();
            rg.setMaTaiSan(ts.getMaTaiSan());
            rg.setTenTaiSan(ts.getTenTaiSan());
            rg.setThongTinChiTiet(ts.getThongTinChiTiet());
            return rg;
        }).collect(Collectors.toList()));
        
        return chiTiet;
    }

    private CanBoTomTat mapToTomTat(CanBoChienSi entity) {
        CanBoTomTat tomTat = new CanBoTomTat();
        tomTat.setId(entity.getId());
        tomTat.setMaCanBo(entity.getMaCanBo());
        tomTat.setCapBac(entity.getCapBac());
        tomTat.setChucVu(entity.getChucVu());
        tomTat.setMaDonVi(entity.getMaDonVi());
        tomTat.setTenDonVi(getTenDanhMuc(entity.getMaDonVi()));
        if (entity.getCongDan() != null) {
            tomTat.setHoTen(entity.getCongDan().getHoTen());
            tomTat.setNgaySinh(entity.getCongDan().getNgaySinh());
            tomTat.setGioiTinh(entity.getCongDan().getGioiTinh());
            tomTat.setMaCongDan(entity.getCongDan().getId());
        }
        return tomTat;
    }

    private String getTenDanhMuc(String ma) {
        if (ma == null || ma.isEmpty()) return null;
        return "Tên danh mục " + ma;
    }
    
    private String getTenCanBo(String id) {
        if (id == null || id.isEmpty()) return null;
        return "Tên cán bộ " + id;
    }
}
