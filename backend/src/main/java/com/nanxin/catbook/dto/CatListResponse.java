package com.nanxin.catbook.dto;

import lombok.Data;
import java.util.List;

@Data
public class CatListResponse {
    private List<CatItem> content;
    private int page; private int size;
    private long total; private int totalPages;
}