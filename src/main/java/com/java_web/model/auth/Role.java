package com.java_web.model.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roleName;
    private String description;


    public Role(String roleName) { this.roleName = roleName; }

}
