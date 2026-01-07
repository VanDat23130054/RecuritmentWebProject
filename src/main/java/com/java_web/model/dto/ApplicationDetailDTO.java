package com.java_web.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer applicationId;
    private Integer jobId;
    private Integer candidateId;
    private String candidateEmail;
    private String candidateName;
    private String candidateSummary;
    private String candidateCity;
    private String coverLetter;
    private String source;
    private Timestamp appliedAt;
    private String status;
    private Integer resumeId;
    private String jobTitle;
    private String jobDescription;
    private String companyName;
    private String resumeFileName;
    private String resumeFileUrl;
    private String recruiterNote;
}
