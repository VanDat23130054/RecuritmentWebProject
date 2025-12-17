package com.java_web.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RelatedJobDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobId;
    private String title;
    private String slug;
    private String companyName;
    private String logoUrl;
    private String cityName;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}
