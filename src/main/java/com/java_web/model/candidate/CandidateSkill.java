package com.java_web.model.candidate;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CandidateSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer candidateSkillId;
    private Integer candidateId;
    private Integer skillId;
    private Short proficiency;
    private Double yearsExperience;

    public CandidateSkill(Integer candidateSkillId) { this.candidateSkillId = candidateSkillId; }


}
