package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class SeniorityLevel implements Serializable {
    private static final long serialVersionUID = 1L;
    private Short seniorityLevelId;
    private String name;

    public SeniorityLevel(Short id) { this.seniorityLevelId = id; }


}
