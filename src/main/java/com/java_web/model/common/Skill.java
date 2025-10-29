package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer skillId;
    private String name;
    private String slug;

    public Skill(Integer skillId) { this.skillId = skillId; }


}
