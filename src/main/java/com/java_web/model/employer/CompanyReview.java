package com.java_web.model.employer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CompanyReview implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer companyReviewId;
    private Integer companyId;
    private Integer candidateId;
    private Short rating;
    private String title;
    private String body;
    private String pros;
    private String cons;
    private boolean isAnonymous;
    private LocalDateTime createdAt;

    public CompanyReview(Integer companyReviewId) {
        this.companyReviewId = companyReviewId;
    }


}
