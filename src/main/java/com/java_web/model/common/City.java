package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer cityId;
    private Integer provinceId;
    private String name;

    public City(Integer cityId) { this.cityId = cityId; }

}
