package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class JobStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private Short jobStatusId;
    private String name;

    public JobStatus(Short id) { this.jobStatusId = id; }

}
