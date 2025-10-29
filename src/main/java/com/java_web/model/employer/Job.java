package com.java_web.model.employer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Job implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer jobId;
    private Integer companyId;
    private Integer recruiterId;
    private String title;
    private String slug;
    private String shortDescription;
    private String description;
    private String responsibilities;
    private String requirements;
    private String benefits;
    private Short employmentTypeId;
    private Short seniorityLevelId;
    private Short remoteTypeId;
    private Integer cityId;
    private Integer countryId;
    private Double salaryMin;
    private Double salaryMax;
    private String currency;
    private LocalDateTime postedAt;
    private LocalDateTime expiresAt;
    private Short statusId;
    private boolean isFeatured;
    private Integer viewsCount;
    private Integer applicationsCount;

    public Job(Integer jobId) {
        this.jobId = jobId;
    }

}
