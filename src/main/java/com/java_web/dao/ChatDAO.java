package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.system.Conversation;
import com.java_web.model.system.Message;

public class ChatDAO {

    /**
     * Get or create a conversation between candidate and recruiter
     */
    public Conversation getOrCreateConversation(Integer candidateId, Integer recruiterId, Integer jobId)
            throws SQLException {
        String sql = "{call system.sp_GetOrCreateConversation(?, ?, ?)}";

        System.out.println("[ChatDAO DEBUG] getOrCreateConversation called with:");
        System.out.println("[ChatDAO DEBUG] candidateId: " + candidateId);
        System.out.println("[ChatDAO DEBUG] recruiterId: " + recruiterId);
        System.out.println("[ChatDAO DEBUG] jobId: " + jobId);

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, candidateId);
            stmt.setInt(2, recruiterId);
            if (jobId != null) {
                stmt.setInt(3, jobId);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conversation conv = mapConversation(rs);
                    System.out.println("[ChatDAO DEBUG] Conversation created/found: " + conv.getConversationId());
                    return conv;
                }
            }
        }
        System.out.println("[ChatDAO DEBUG] No conversation returned!");
        return null;
    }

    /**
     * Get conversation by ID
     */
    public Conversation getConversationById(Integer conversationId) throws SQLException {
        String sql = "{call system.sp_GetConversationById(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, conversationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapConversation(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get all conversations for a user
     */
    public List<Conversation> getUserConversations(Integer userId, String userRole) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "{call system.sp_GetUserConversations(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, userRole);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conversation conv = mapConversation(rs);
                    // Additional fields for list view
                    conv.setLastMessage(rs.getString("LastMessage"));
                    conv.setUnreadCount(rs.getInt("UnreadCount"));
                    conversations.add(conv);
                }
            }
        }
        return conversations;
    }

    /**
     * Get messages for a conversation
     */
    public List<Message> getConversationMessages(Integer conversationId, int pageNumber, int pageSize)
            throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "{call system.sp_GetConversationMessages(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, conversationId);
            stmt.setInt(2, pageNumber);
            stmt.setInt(3, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }
        }
        return messages;
    }

    /**
     * Send a new message
     */
    public Message sendMessage(Integer conversationId, Integer senderId, String body) throws SQLException {
        String sql = "{call system.sp_SendMessage(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, body);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMessage(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mark messages as read for a user in a conversation
     */
    public int markMessagesAsRead(Integer conversationId, Integer userId) throws SQLException {
        String sql = "{call system.sp_MarkMessagesAsRead(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, conversationId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MessagesMarkedRead");
                }
            }
        }
        return 0;
    }

    /**
     * Get total unread message count for a user
     */
    public int getUnreadMessageCount(Integer userId, String userRole) throws SQLException {
        String sql = "{call system.sp_GetUnreadMessageCount(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, userRole);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UnreadCount");
                }
            }
        }
        return 0;
    }

    /**
     * Check if user has access to a conversation
     */
    public boolean hasAccessToConversation(Integer conversationId, Integer userId) throws SQLException {
        Conversation conv = getConversationById(conversationId);
        if (conv == null) {
            return false;
        }
        return userId.equals(conv.getCandidateUserId()) || userId.equals(conv.getRecruiterUserId());
    }

    // ==================== Helper Methods ====================
    private Conversation mapConversation(ResultSet rs) throws SQLException {
        Conversation conv = new Conversation();
        conv.setConversationId(rs.getInt("ConversationId"));
        conv.setCandidateId(rs.getInt("CandidateId"));
        conv.setRecruiterId(rs.getInt("RecruiterId"));

        int jobId = rs.getInt("JobId");
        conv.setJobId(rs.wasNull() ? null : jobId);

        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        conv.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        Timestamp lastMessageAt = rs.getTimestamp("LastMessageAt");
        conv.setLastMessageAt(lastMessageAt != null ? lastMessageAt.toLocalDateTime() : null);

        conv.setActive(rs.getBoolean("IsActive"));

        // Candidate info
        conv.setCandidateName(rs.getString("CandidateName"));
        conv.setCandidateAvatar(rs.getString("CandidateAvatar"));
        conv.setCandidateEmail(rs.getString("CandidateEmail"));

        // Try to get CandidateUserId if available
        try {
            conv.setCandidateUserId(rs.getInt("CandidateUserId"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        // Recruiter info
        conv.setRecruiterName(rs.getString("RecruiterName"));
        conv.setRecruiterAvatar(rs.getString("RecruiterAvatar"));
        conv.setRecruiterEmail(rs.getString("RecruiterEmail"));

        // Try to get RecruiterUserId if available
        try {
            conv.setRecruiterUserId(rs.getInt("RecruiterUserId"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        // Company info
        conv.setCompanyName(rs.getString("CompanyName"));
        conv.setCompanyLogo(rs.getString("CompanyLogo"));

        // Job info
        conv.setJobTitle(rs.getString("JobTitle"));

        return conv;
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setMessageId(rs.getLong("MessageId"));
        msg.setConversationId(rs.getInt("ConversationId"));
        msg.setSenderId(rs.getInt("SenderId"));
        msg.setBody(rs.getString("Body"));

        Timestamp sentAt = rs.getTimestamp("SentAt");
        msg.setSentAt(sentAt != null ? sentAt.toLocalDateTime() : null);

        msg.setRead(rs.getBoolean("IsRead"));

        // ReadAt may not be present in all queries
        try {
            Timestamp readAt = rs.getTimestamp("ReadAt");
            msg.setReadAt(readAt != null ? readAt.toLocalDateTime() : null);
        } catch (SQLException e) {
            // Column not in result set, ignore
            msg.setReadAt(null);
        }

        // Sender info
        msg.setSenderEmail(rs.getString("SenderEmail"));
        msg.setSenderName(rs.getString("SenderName"));
        msg.setSenderAvatar(rs.getString("SenderAvatar"));
        msg.setSenderRole(rs.getString("SenderRole"));

        return msg;
    }

    /**
     * Search for recruiters by name or email
     */
    public List<Object> searchRecruiters(String searchQuery) throws SQLException {
        List<Object> results = new ArrayList<>();
        String sql = "SELECT TOP 20 "
                + "r.RecruiterId as id, "
                + "COALESCE(r.FirstName + ' ' + r.LastName, u.Email) as name, "
                + "u.Email as email "
                + "FROM employer.Recruiters r "
                + "INNER JOIN auth.Users u ON r.UserId = u.UserId "
                + "WHERE r.FirstName LIKE ? OR r.LastName LIKE ? OR u.Email LIKE ? "
                + "AND r.IsActive = 1 "
                + "ORDER BY r.FirstName, r.LastName";

        try (Connection conn = DB.getConnection(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchQuery + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("name", rs.getString("name"));
                    item.put("email", rs.getString("email"));
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching recruiters: " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Search for candidates by name or email
     */
    public List<Object> searchCandidates(String searchQuery) throws SQLException {
        List<Object> results = new ArrayList<>();
        String sql = "SELECT TOP 20 "
                + "c.CandidateId as id, "
                + "COALESCE(c.FullName, u.Email) as name, "
                + "u.Email as email "
                + "FROM candidate.Candidates c "
                + "INNER JOIN auth.Users u ON c.UserId = u.UserId "
                + "WHERE c.FullName LIKE ? OR u.Email LIKE ? "
                + "AND u.IsActive = 1 "
                + "ORDER BY c.FullName";

        try (Connection conn = DB.getConnection(); java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchQuery + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("name", rs.getString("name"));
                    item.put("email", rs.getString("email"));
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching candidates: " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Get recruiters from jobs that a candidate has applied to
     * (application-based contacts) This is the preferred way for candidates to
     * find who they can message
     */
    public List<Object> getAppliedJobRecruiters(Integer candidateId, String searchQuery) throws SQLException {
        List<Object> results = new ArrayList<>();

        // Build SQL dynamically based on whether search query exists
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT TOP 20 ")
                .append("r.RecruiterId as id, ")
                .append("COALESCE(r.FirstName + ' ' + r.LastName, u.Email) as name, ")
                .append("u.Email as email, ")
                .append("j.JobId as jobId, ")
                .append("j.Title as jobTitle, ")
                .append("c2.CompanyName as companyName ")
                .append("FROM candidate.Applications a ")
                .append("INNER JOIN employer.Jobs j ON a.JobId = j.JobId ")
                .append("INNER JOIN employer.Recruiters r ON j.RecruiterId = r.RecruiterId ")
                .append("INNER JOIN auth.Users u ON r.UserId = u.UserId ")
                .append("INNER JOIN employer.Companies c2 ON j.CompanyId = c2.CompanyId ")
                .append("WHERE a.CandidateId = ? ");

        boolean hasSearch = searchQuery != null && !searchQuery.trim().isEmpty();
        if (hasSearch) {
            sqlBuilder.append("AND (r.FirstName LIKE ? OR r.LastName LIKE ? OR u.Email LIKE ? OR j.Title LIKE ?) ");
        }
        sqlBuilder.append("ORDER BY j.Title");

        try (Connection conn = DB.getConnection(); java.sql.PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            stmt.setInt(1, candidateId);
            if (hasSearch) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
                stmt.setString(5, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("name", rs.getString("name"));
                    item.put("email", rs.getString("email"));
                    item.put("jobId", rs.getInt("jobId"));
                    item.put("jobTitle", rs.getString("jobTitle"));
                    item.put("companyName", rs.getString("companyName"));
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting applied job recruiters: " + e.getMessage());
            throw e;
        }

        return results;
    }

    /**
     * Get candidates who have applied to a recruiter's jobs (application-based
     * contacts) This is the preferred way for recruiters to find who they can
     * message
     */
    public List<Object> getJobApplicantCandidates(Integer recruiterId, String searchQuery) throws SQLException {
        List<Object> results = new ArrayList<>();

        // Build SQL dynamically based on whether search query exists
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT TOP 20 ")
                .append("c.CandidateId as id, ")
                .append("COALESCE(c.FullName, u.Email) as name, ")
                .append("u.Email as email, ")
                .append("j.JobId as jobId, ")
                .append("j.Title as jobTitle, ")
                .append("a.Status as applicationStatus ")
                .append("FROM candidate.Applications a ")
                .append("INNER JOIN employer.Jobs j ON a.JobId = j.JobId ")
                .append("INNER JOIN candidate.Candidates c ON a.CandidateId = c.CandidateId ")
                .append("INNER JOIN auth.Users u ON c.UserId = u.UserId ")
                .append("WHERE j.RecruiterId = ? ");

        boolean hasSearch = searchQuery != null && !searchQuery.trim().isEmpty();
        if (hasSearch) {
            sqlBuilder.append("AND (c.FullName LIKE ? OR u.Email LIKE ? OR j.Title LIKE ?) ");
        }
        sqlBuilder.append("ORDER BY a.AppliedAt DESC");

        try (Connection conn = DB.getConnection(); java.sql.PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            stmt.setInt(1, recruiterId);
            if (hasSearch) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.LinkedHashMap<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("name", rs.getString("name"));
                    item.put("email", rs.getString("email"));
                    item.put("jobId", rs.getInt("jobId"));
                    item.put("jobTitle", rs.getString("jobTitle"));
                    item.put("applicationStatus", rs.getString("applicationStatus"));
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting job applicant candidates: " + e.getMessage());
            throw e;
        }

        return results;
    }
}
