package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class EmploymentType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Short employmentTypeId;
    private String name;

    public EmploymentType(Short id) { this.employmentTypeId = id; }

}
