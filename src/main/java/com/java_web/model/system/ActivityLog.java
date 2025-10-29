package com.java_web.model.system;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long activityId;
    private Integer userId;
    private String action;
    private String details;
    private LocalDateTime createdAt;

    public ActivityLog(Long activityId) {
        this.activityId = activityId;
    }


}
