package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer countryId;
    private String name;
    private String isoCode;

    public Country(Integer countryId) { this.countryId = countryId; }

}
