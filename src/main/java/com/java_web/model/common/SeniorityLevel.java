package com.java_web.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeniorityLevel implements Serializable {

    private static final long serialVersionUID = 1L;
    private Short seniorityLevelId;
    private String name;

    public SeniorityLevel(Short id) {
        this.seniorityLevelId = id;
    }

}
