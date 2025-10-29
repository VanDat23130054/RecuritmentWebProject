package com.java_web.model.employer;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer companyId;
    private String name;
    private String shortName;
    private String website;
    private String description;
    private String industry;
    private String sizeRange;
    private Short foundedYear;
    private Integer headquartersCityId;
    private String logoUrl;
    private boolean isVerified;
    private LocalDateTime createdAt;

    public Company(Integer companyId) {
        this.companyId = companyId;
    }

}
