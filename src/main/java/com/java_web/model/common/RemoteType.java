package com.java_web.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RemoteType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Short remoteTypeId;
    private String name;

    public RemoteType(Short id) { this.remoteTypeId = id; }




}
