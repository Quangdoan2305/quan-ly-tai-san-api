package com.gtelict.phuong_tien_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    @Schema(example = "10")
    private int pageSize;
    
    @Schema(example = "0")
    private int pageNumber;
    
    @Schema(example = "1")
    private int totalPages;
    
    @Schema(example = "5")
    private long totalElements;
    
    private List<T> content;

    public PageResponse(Page<T> page) {
        this.pageSize = page.getSize();
        this.pageNumber = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.content = page.getContent();
    }
}
