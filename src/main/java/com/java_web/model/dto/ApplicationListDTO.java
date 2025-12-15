package com.java_web.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer applicationId;
    private Integer jobId;
    private String jobTitle;
    private Integer candidateId;
    private String candidateName;
    private String candidateEmail;
    private String companyName;
    private String coverLetter;
    private String source;
    private Timestamp appliedAt;
    private String status;
    private Integer resumeId;
    private String fileUrl;
    private String fileName;
}
