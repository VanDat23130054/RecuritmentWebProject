package com.java_web.model.employer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class JobSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer jobSkillId;
    private Integer jobId;
    private Integer skillId;
    private boolean isRequired;
    private Short weight;

    public JobSkill(Integer jobSkillId) {
        this.jobSkillId = jobSkillId;
    }

}
