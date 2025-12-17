package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer companyId;
    private String name;
    private String slug;
    private String logoUrl;
    private String websiteUrl;
    private String description;
    private String industry;
    private String sizeRange;
    private Integer foundedYear;
    private Integer headquartersCityId;
    private String address;
    private String cityName;
    private Integer activeJobCount;
}
