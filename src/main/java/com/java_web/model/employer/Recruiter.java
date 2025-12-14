package com.java_web.model.employer;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Recruiter implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer recruiterId;
    private Integer userId;   // FK -> auth.Users.userId
    private Integer companyId;
    private String title;
    private boolean isPrimaryContact;

    public Recruiter(Integer recruiterId) {
        this.recruiterId = recruiterId;
    }

}
