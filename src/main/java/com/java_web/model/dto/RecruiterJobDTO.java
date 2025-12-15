package com.java_web.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruiterJobDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobId;
    private String title;
    private String slug;
    private String cityName;
    private Byte statusId;
    private String status;
    private Timestamp postedAt;
    private Timestamp expiresAt;
    private Integer viewsCount;
    private Integer applicationsCount;
    private Boolean isFeatured;
    private String employmentType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
}
