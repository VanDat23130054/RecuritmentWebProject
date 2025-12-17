package com.java_web.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResumeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer resumeId;
    private Integer candidateId;
    private String driveFileId;
    private String fileName;
    private String fileUrl;
    private String parsedJson;
    private Boolean isPrimary;
    private Boolean isPublic;
    private Timestamp uploadedAt;
}
