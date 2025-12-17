package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSkillDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer skillId;
    private String skillName;
}
