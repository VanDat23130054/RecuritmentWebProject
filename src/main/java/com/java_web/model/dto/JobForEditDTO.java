package com.java_web.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobForEditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobId;
    private Integer companyId;
    private Integer recruiterId;
    private String title;
    private String slug;
    private String description;
    private String requirements;
    private String benefits;
    private Integer cityId;
    private Byte employmentTypeId;
    private Byte seniorityLevelId;
    private Byte remoteTypeId;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private Byte statusId;
    private Boolean isFeatured;
    private Timestamp postedAt;
    private Timestamp expiresAt;
    private Integer viewsCount;
    private Integer applicationsCount;
    private List<JobSkillDTO> skills;
}
