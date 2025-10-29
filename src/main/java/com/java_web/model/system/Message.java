// package com.jobboard.model.system;
package com.java_web.model.system;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long messageId;
    private Integer fromUserId;
    private Integer toUserId;
    private Integer jobId;
    private String body;
    private LocalDateTime sentAt;
    private boolean isRead;

    public Message(Long messageId) {
        this.messageId = messageId;
    }
}
