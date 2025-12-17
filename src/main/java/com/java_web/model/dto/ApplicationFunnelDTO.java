package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationFunnelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalViews;
    private Integer totalApplications;
    private Integer underReview;
    private Integer interviewed;
    private Integer offered;
    private Double viewToAppRate;
    private Double appToReviewRate;
    private Double reviewToInterviewRate;
    private Double interviewToOfferRate;
}
