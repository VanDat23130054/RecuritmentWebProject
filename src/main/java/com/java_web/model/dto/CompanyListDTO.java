package com.java_web.model.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer companyId;
    private String name;
    private String logoUrl;
    private String cityName;
    private Integer activeJobCount;
    private String topSkills; // JSON string of skills

    // Transient field populated in servlet
    private List<Map<String, Object>> topSkillsList;
}
