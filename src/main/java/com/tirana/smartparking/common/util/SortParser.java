package com.tirana.smartparking.common.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortParser {

    public Sort parseSort(String sort) {
        // Example sort string: "property1,asc;property2,desc"
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        String[] sortParams = sort.split(";");
        Sort sortResult = Sort.unsorted();
        for (String param : sortParams) {
            String[] parts = param.split(",");
            if (parts.length == 2) {
                String property = parts[0].trim();
                try {
                    Sort.Direction direction = Sort.Direction.fromString(parts[1].trim());
                    sortResult = sortResult.and(Sort.by(direction, property));
                } catch (IllegalArgumentException ex) {
                    // Log or ignore invalid directions
                }
            }
        }
        return sortResult;
    }
}
