package com.java_web.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobId;
    private String title;
    private String slug;
    private Integer companyId;
    private String companyName;
    private String logoUrl;
    private String cityName;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private Boolean isFeatured;
    private String skills; // JSON string of skills

    // Transient fields populated in servlet
    private List<Map<String, Object>> skillsList;
    private Boolean isSaved;
}
