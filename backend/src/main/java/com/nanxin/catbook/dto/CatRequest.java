package com.nanxin.catbook.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CatRequest {
    @NotBlank private String name;
    private String nickname; private String gender; private Integer birthYear;
    private String colour; private String colourTags; private String locationArea;
    private String locationDetail; private String personalityTags;
    private String personalityDesc; private Boolean sterilized;
    private String status; private Long fatherId; private Long motherId;
    private String notes; private BigDecimal weight;
    private Long coverPhotoId;
}