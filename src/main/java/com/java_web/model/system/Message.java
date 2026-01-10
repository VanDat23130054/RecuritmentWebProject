// package com.jobboard.model.system;
package com.java_web.model.system;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Integer conversationId;
    private Integer senderId;
    private String body;
    private LocalDateTime sentAt;
    private boolean isRead;
    private LocalDateTime readAt;

    // Sender info (populated from join)
    private String senderEmail;
    private String senderName;
    private String senderAvatar;
    private String senderRole;

    public Message(Long messageId) {
        this.messageId = messageId;
    }

    /**
     * Check if the message was sent by the specified user
     */
    public boolean isSentBy(Integer userId) {
        return senderId != null && senderId.equals(userId);
    }
}
