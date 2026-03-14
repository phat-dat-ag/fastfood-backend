package com.example.fastfoodshop.response.award;

import com.example.fastfoodshop.dto.AwardDTO;
import com.example.fastfoodshop.entity.Award;
import org.springframework.data.domain.Page;

import java.util.List;

public record AwardPageResponse(
        List<AwardDTO> awards,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {
    public static AwardPageResponse from(Page<Award> page) {
        List<AwardDTO> awards = page.getContent().stream().map(AwardDTO::from).toList();

        return new AwardPageResponse(
                awards,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
