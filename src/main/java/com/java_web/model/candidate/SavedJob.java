package com.java_web.model.candidate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SavedJob implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer savedJobId;
    private Integer userId;
    private Integer jobId;
    private LocalDateTime savedAt;

    public SavedJob(Integer savedJobId) {
        this.savedJobId = savedJobId;
    }


}
