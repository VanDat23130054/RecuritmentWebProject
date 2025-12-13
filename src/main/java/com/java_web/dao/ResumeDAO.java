package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.java_web.config.DB;

/**
 * Data Access Object for Resume operations Handles database operations for
 * candidate resumes stored in Google Drive
 */
public class ResumeDAO {

    /**
     * Get CandidateId by UserId
     *
     * @param userId The user's ID from auth.Users table
     * @return CandidateId if found, null otherwise
     */
    public Integer getCandidateIdByUserId(int userId) throws SQLException {
        String sql = "{call candidate.sp_GetCandidateIdByUserId(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.registerOutParameter(2, Types.INTEGER);

            stmt.execute();

            int candidateId = stmt.getInt(2);

            // Check if the value was NULL in the database
            if (stmt.wasNull()) {
                return null;
            }

            return candidateId;
        }
    }

    /**
     * Save resume information to database
     *
     * @param candidateId ID of the candidate
     * @param driveFileId Google Drive file ID
     * @param fileName Name of the resume file
     * @param fileUrl Download URL from Google Drive
     * @return resumeId of the inserted record
     */
    public int saveResume(int candidateId, String driveFileId, String fileName, String fileUrl) throws SQLException {
        String sql = "INSERT INTO candidate.Resumes (CandidateId, DriveFileId, FileName, FileUrl, IsPrimary, IsPublic, UploadedAt) "
                + "VALUES (?, ?, ?, ?, 1, 1, GETDATE())";

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, candidateId);
            stmt.setString(2, driveFileId);
            stmt.setString(3, fileName);
            stmt.setString(4, fileUrl);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating resume failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating resume failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Get resume by resumeId
     *
     * @param resumeId ID of the resume
     * @return Map containing resume details
     */
    public Map<String, Object> getResume(int resumeId) throws SQLException {
        String sql = "SELECT ResumeId, CandidateId, DriveFileId, FileName, FileUrl, ParsedJson, "
                + "IsPrimary, IsPublic, UploadedAt FROM candidate.Resumes WHERE ResumeId = ?";

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resumeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> resume = new HashMap<>();
                    resume.put("resumeId", rs.getInt("ResumeId"));
                    resume.put("candidateId", rs.getInt("CandidateId"));
                    resume.put("driveFileId", rs.getString("DriveFileId"));
                    resume.put("fileName", rs.getString("FileName"));
                    resume.put("fileUrl", rs.getString("FileUrl"));
                    resume.put("parsedJson", rs.getString("ParsedJson"));
                    resume.put("isPrimary", rs.getBoolean("IsPrimary"));
                    resume.put("isPublic", rs.getBoolean("IsPublic"));
                    resume.put("uploadedAt", rs.getTimestamp("UploadedAt"));
                    return resume;
                }
            }
        }
        return null;
    }

    /**
     * Get all resumes for a candidate
     *
     * @param candidateId ID of the candidate
     * @return List of resume maps
     */
    public List<Map<String, Object>> getResumesByCandidateId(int candidateId) throws SQLException {
        String sql = "SELECT ResumeId, CandidateId, DriveFileId, FileName, FileUrl, ParsedJson, "
                + "IsPrimary, IsPublic, UploadedAt FROM candidate.Resumes WHERE CandidateId = ? "
                + "ORDER BY IsPrimary DESC, UploadedAt DESC";

        List<Map<String, Object>> resumes = new ArrayList<>();

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> resume = new HashMap<>();
                    resume.put("resumeId", rs.getInt("ResumeId"));
                    resume.put("candidateId", rs.getInt("CandidateId"));
                    resume.put("driveFileId", rs.getString("DriveFileId"));
                    resume.put("fileName", rs.getString("FileName"));
                    resume.put("fileUrl", rs.getString("FileUrl"));
                    resume.put("parsedJson", rs.getString("ParsedJson"));
                    resume.put("isPrimary", rs.getBoolean("IsPrimary"));
                    resume.put("isPublic", rs.getBoolean("IsPublic"));
                    resume.put("uploadedAt", rs.getTimestamp("UploadedAt"));
                    resumes.add(resume);
                }
            }
        }
        return resumes;
    }

    /**
     * Update Google Drive file ID for an existing resume Used during migration
     * from local storage to Google Drive
     *
     * @param resumeId ID of the resume
     * @param driveFileId Google Drive file ID
     * @param fileUrl Download URL from Google Drive
     */
    public void updateDriveFileId(int resumeId, String driveFileId, String fileUrl) throws SQLException {
        String sql = "UPDATE candidate.Resumes SET DriveFileId = ?, FileUrl = ? WHERE ResumeId = ?";

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, driveFileId);
            stmt.setString(2, fileUrl);
            stmt.setInt(3, resumeId);

            stmt.executeUpdate();
        }
    }

    /**
     * Delete resume record from database
     *
     * @param resumeId ID of the resume
     */
    public void deleteResume(int resumeId) throws SQLException {
        String sql = "DELETE FROM candidate.Resumes WHERE ResumeId = ?";

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, resumeId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get all resumes (for migration purposes)
     *
     * @return List of all resumes
     */
    public List<Map<String, Object>> getAllResumes() throws SQLException {
        String sql = "SELECT ResumeId, CandidateId, DriveFileId, FileName, FileUrl, ParsedJson, "
                + "IsPrimary, IsPublic, UploadedAt FROM candidate.Resumes";

        List<Map<String, Object>> resumes = new ArrayList<>();

        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> resume = new HashMap<>();
                resume.put("resumeId", rs.getInt("ResumeId"));
                resume.put("candidateId", rs.getInt("CandidateId"));
                resume.put("driveFileId", rs.getString("DriveFileId"));
                resume.put("fileName", rs.getString("FileName"));
                resume.put("fileUrl", rs.getString("FileUrl"));
                resume.put("parsedJson", rs.getString("ParsedJson"));
                resume.put("isPrimary", rs.getBoolean("IsPrimary"));
                resume.put("isPublic", rs.getBoolean("IsPublic"));
                resume.put("uploadedAt", rs.getTimestamp("UploadedAt"));
                resumes.add(resume);
            }
        }
        return resumes;
    }

    /**
     * Set a resume as primary for a candidate
     *
     * @param resumeId ID of the resume to set as primary
     * @param candidateId ID of the candidate
     */
    public void setPrimaryResume(int resumeId, int candidateId) throws SQLException {
        Connection conn = null;
        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);

            // First, set all resumes for this candidate as non-primary
            String sql1 = "UPDATE candidate.Resumes SET IsPrimary = 0 WHERE CandidateId = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                stmt1.setInt(1, candidateId);
                stmt1.executeUpdate();
            }

            // Then set the specified resume as primary
            String sql2 = "UPDATE candidate.Resumes SET IsPrimary = 1 WHERE ResumeId = ? AND CandidateId = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                stmt2.setInt(1, resumeId);
                stmt2.setInt(2, candidateId);
                stmt2.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
