package com.java_web.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer countryId;
    private String name;
    private String isoCode;

    public Country(Integer countryId) {
        this.countryId = countryId;
    }

}
