package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.java_web.config.DB;

public class ApplicationDAO {

    /**
     * Get all applications for a recruiter's jobs with filters and pagination
     */
    public List<Map<String, Object>> getApplicationsByRecruiter(Integer recruiterId, Integer jobId,
            String status, String keyword, int pageNumber, int pageSize) throws SQLException {
        List<Map<String, Object>> applications = new ArrayList<>();
        String sql = "{call employer.sp_GetApplicationsByRecruiter(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            if (jobId != null) {
                stmt.setInt(2, jobId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(3, status);
            } else {
                stmt.setNull(3, Types.NVARCHAR);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(4, keyword);
            } else {
                stmt.setNull(4, Types.NVARCHAR);
            }

            stmt.setInt(5, pageNumber);
            stmt.setInt(6, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> app = new HashMap<>();
                    app.put("applicationId", rs.getInt("ApplicationId"));
                    app.put("jobId", rs.getInt("JobId"));
                    app.put("jobTitle", rs.getString("JobTitle"));
                    app.put("candidateId", rs.getInt("CandidateId"));
                    app.put("candidateName", rs.getString("CandidateName"));
                    app.put("candidateEmail", rs.getString("CandidateEmail"));
                    app.put("coverLetter", rs.getString("CoverLetter"));
                    app.put("source", rs.getString("Source"));
                    app.put("appliedAt", rs.getTimestamp("AppliedAt"));
                    app.put("status", rs.getString("Status"));
                    app.put("resumeId", rs.getObject("ResumeId"));
                    app.put("fileUrl", rs.getString("FileUrl"));
                    app.put("fileName", rs.getString("FileName"));
                    applications.add(app);
                }
            }
        }
        return applications;
    }

    /**
     * Get total application count for pagination
     */
    public int getApplicationCountByRecruiter(Integer recruiterId, Integer jobId,
            String status, String keyword) throws SQLException {
        String sql = "{call employer.sp_GetApplicationCountByRecruiter(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            if (jobId != null) {
                stmt.setInt(2, jobId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(3, status);
            } else {
                stmt.setNull(3, Types.NVARCHAR);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(4, keyword);
            } else {
                stmt.setNull(4, Types.NVARCHAR);
            }

            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();

            return stmt.getInt(5);
        }
    }

    /**
     * Get detailed application information
     */
    public Map<String, Object> getApplicationDetail(Integer applicationId, Integer recruiterId)
            throws SQLException {
        String sql = "{call employer.sp_GetApplicationDetail(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> app = new HashMap<>();
                    app.put("applicationId", rs.getInt("ApplicationId"));
                    app.put("jobId", rs.getInt("JobId"));
                    app.put("jobTitle", rs.getString("JobTitle"));
                    app.put("candidateId", rs.getInt("CandidateId"));
                    app.put("candidateName", rs.getString("CandidateName"));
                    app.put("candidateEmail", rs.getString("CandidateEmail"));
                    app.put("candidatePhone", rs.getString("CandidatePhone"));
                    app.put("coverLetter", rs.getString("CoverLetter"));
                    app.put("source", rs.getString("Source"));
                    app.put("appliedAt", rs.getTimestamp("AppliedAt"));
                    app.put("status", rs.getString("Status"));
                    app.put("statusUpdatedAt", rs.getTimestamp("StatusUpdatedAt"));
                    app.put("resumeId", rs.getObject("ResumeId"));
                    app.put("fileUrl", rs.getString("FileUrl"));
                    app.put("fileName", rs.getString("FileName"));
                    app.put("resumeData", rs.getString("ResumeData"));
                    return app;
                }
            }
        }
        return null;
    }

    /**
     * Update application status
     */
    public boolean updateApplicationStatus(Integer applicationId, Integer recruiterId,
            String newStatus) throws SQLException {
        String sql = "{call employer.sp_UpdateApplicationStatus(?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, recruiterId);
            stmt.setString(3, newStatus);

            // Register OUTPUT parameter
            stmt.registerOutParameter(4, java.sql.Types.BIT);

            stmt.execute();

            // Get the success flag from OUTPUT parameter
            boolean success = stmt.getBoolean(4);
            return success;
        }
    }

    /**
     * Get application status counts for a recruiter
     */
    public Map<String, Integer> getApplicationStatusCounts(Integer recruiterId) throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "{call employer.sp_GetApplicationStatusCounts(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    counts.put("all", rs.getInt("allCount"));
                    counts.put("applied", rs.getInt("appliedCount"));
                    counts.put("underReview", rs.getInt("underReviewCount"));
                    counts.put("interview", rs.getInt("interviewCount"));
                    counts.put("rejected", rs.getInt("rejectedCount"));
                }
            }
        }
        return counts;
    }
}
