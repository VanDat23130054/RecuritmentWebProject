package com.java_web.model.auth;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleName;
    private String description;

    public Role(String roleName) {
        this.roleName = roleName;
    }

}
