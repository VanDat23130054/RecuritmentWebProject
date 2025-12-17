package com.java_web.model.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationStatusCountsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer all;
    private Integer applied;
    private Integer underReview;
    private Integer interview;
    private Integer rejected;
}
