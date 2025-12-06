package com.java_web.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer skillId;
    private String name;
    private String slug;

    public Skill(Integer skillId) {
        this.skillId = skillId;
    }

}
