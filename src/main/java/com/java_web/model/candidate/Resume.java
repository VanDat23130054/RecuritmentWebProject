package com.java_web.model.candidate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Resume implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer resumeId;
    private Integer candidateId;
    private String fileUrl;
    private String fileName;
    private String parsedJson;
    private boolean isPrimary;
    private boolean isPublic;
    private LocalDateTime uploadedAt;

    public Resume(Integer resumeId) {
        this.resumeId = resumeId;
    }

}
