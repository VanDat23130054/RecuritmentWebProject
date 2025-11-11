package com.java_web.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer provinceId;
    private Integer countryId;
    private String name;

    public Province(Integer provinceId) {
        this.provinceId = provinceId;
    }

}
