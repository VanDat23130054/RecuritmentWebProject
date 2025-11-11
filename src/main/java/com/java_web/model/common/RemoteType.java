package com.java_web.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RemoteType implements Serializable {

    private static final long serialVersionUID = 1L;
    private Short remoteTypeId;
    private String name;

    public RemoteType(Short id) {
        this.remoteTypeId = id;
    }

}
