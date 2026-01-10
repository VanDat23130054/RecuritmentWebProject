package com.java_web.model.system;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Conversation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer conversationId;
    private Integer candidateId;
    private Integer recruiterId;
    private Integer jobId;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private boolean isActive;

    // Candidate info
    private String candidateName;
    private String candidateAvatar;
    private Integer candidateUserId;
    private String candidateEmail;

    // Recruiter info
    private String recruiterName;
    private String recruiterAvatar;
    private Integer recruiterUserId;
    private String recruiterEmail;

    // Company info
    private String companyName;
    private String companyLogo;

    // Job info
    private String jobTitle;

    // Computed fields
    private String lastMessage;
    private int unreadCount;

    public Conversation(Integer conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Get the display name for the other participant based on current user role
     */
    public String getOtherParticipantName(String currentUserRole) {
        if ("Candidate".equals(currentUserRole)) {
            return recruiterName;
        }
        return candidateName;
    }

    /**
     * Get the avatar for the other participant based on current user role
     */
    public String getOtherParticipantAvatar(String currentUserRole) {
        if ("Candidate".equals(currentUserRole)) {
            return recruiterAvatar;
        }
        return candidateAvatar;
    }
}
