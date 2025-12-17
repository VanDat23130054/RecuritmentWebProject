package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruiterDashboardStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalJobs;
    private Integer activeJobs;
    private Integer totalApplications;
    private Integer newApplications;
    private Integer interviewsScheduled;
    private Integer totalViews;
}
