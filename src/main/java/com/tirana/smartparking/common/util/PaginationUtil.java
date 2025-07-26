package com.tirana.smartparking.common.util;

import com.tirana.smartparking.common.dto.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginationUtil<T> {

    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.hasContent()
        );
    }

}
