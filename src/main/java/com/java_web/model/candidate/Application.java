package com.java_web.model.candidate;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer applicationId;
    private Integer jobId;
    private Integer candidateId;
    private Integer resumeId;
    private String coverLetter;
    private String source;
    private LocalDateTime appliedAt;
    private String status; // Applied, Viewed, Interview, Offered, Rejected, Hired
    private String recruiterNote;

    public Application(Integer applicationId) {
        this.applicationId = applicationId;
    }

}