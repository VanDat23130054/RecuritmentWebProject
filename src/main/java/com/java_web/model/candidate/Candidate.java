// package com.jobboard.model.candidate;
package com.java_web.model.candidate;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Candidate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer candidateId;
    private Integer userId;
    private String fullName;
    private String headline;
    private String summary;
    private Double yearsOfExperience;
    private Integer cityId;
    private Integer countryId;
    private String avatarUrl;
    private boolean publicProfile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for JSP binding (not persisted to DB)
    private String cityName;
    private String countryName;

    public Candidate(Integer candidateId) {
        this.candidateId = candidateId;
    }

}
