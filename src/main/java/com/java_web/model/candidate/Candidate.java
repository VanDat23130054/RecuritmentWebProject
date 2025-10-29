// package com.jobboard.model.candidate;
package com.java_web.model.candidate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    public Candidate(Integer candidateId) { this.candidateId = candidateId; }


}
