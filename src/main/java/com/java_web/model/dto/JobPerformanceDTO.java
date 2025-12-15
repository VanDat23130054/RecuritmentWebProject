package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobPerformanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobId;
    private String title;
    private Integer viewsCount;
    private Integer applicationsCount;
    private Double conversionRate;
    private Integer daysSincePosted;
}
