package com.java_web.model.dto;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TimelineDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date date;
    private Integer count;
}
