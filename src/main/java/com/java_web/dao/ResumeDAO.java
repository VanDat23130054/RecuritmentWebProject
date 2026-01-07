package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.dto.ResumeDTO;

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
        String sql = "{call candidate.sp_SaveResume(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, candidateId);
            stmt.setString(2, driveFileId);
            stmt.setString(3, fileName);
            stmt.setString(4, fileUrl);
            stmt.registerOutParameter(5, Types.INTEGER);

            stmt.execute();

            int resumeId = stmt.getInt(5);
            if (resumeId == 0) {
                throw new SQLException("Creating resume failed, no ID obtained.");
            }
            return resumeId;
        }
    }

    /**
     * Get resume by resumeId
     *
     * @param resumeId ID of the resume
     * @return ResumeDTO containing resume details
     */
    public ResumeDTO getResume(int resumeId) throws SQLException {
        String sql = "{call candidate.sp_GetResumeById(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, resumeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ResumeDTO resume = new ResumeDTO();
                    resume.setResumeId(rs.getInt("ResumeId"));
                    resume.setCandidateId(rs.getInt("CandidateId"));
                    resume.setDriveFileId(rs.getString("DriveFileId"));
                    resume.setFileName(rs.getString("FileName"));
                    resume.setFileUrl(rs.getString("FileUrl"));
                    resume.setParsedJson(rs.getString("ParsedJson"));
                    resume.setIsPrimary(rs.getBoolean("IsPrimary"));
                    resume.setIsPublic(rs.getBoolean("IsPublic"));
                    resume.setUploadedAt(rs.getTimestamp("UploadedAt"));
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
     * @return List of ResumeDTO objects
     */
    public List<ResumeDTO> getResumesByCandidateId(int candidateId) throws SQLException {
        String sql = "{call candidate.sp_GetResumesByCandidateId(?)}";

        List<ResumeDTO> resumes = new ArrayList<>();

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, candidateId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ResumeDTO resume = new ResumeDTO();
                    resume.setResumeId(rs.getInt("ResumeId"));
                    resume.setCandidateId(rs.getInt("CandidateId"));
                    resume.setDriveFileId(rs.getString("DriveFileId"));
                    resume.setFileName(rs.getString("FileName"));
                    resume.setFileUrl(rs.getString("FileUrl"));
                    resume.setParsedJson(rs.getString("ParsedJson"));
                    resume.setIsPrimary(rs.getBoolean("IsPrimary"));
                    resume.setIsPublic(rs.getBoolean("IsPublic"));

                    java.sql.Timestamp uploadedAt = rs.getTimestamp("UploadedAt");
                    resume.setUploadedAt(uploadedAt != null ? uploadedAt : null);

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
        String sql = "{call candidate.sp_UpdateResumeDriveFileId(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, resumeId);
            stmt.setString(2, driveFileId);
            stmt.setString(3, fileUrl);

            stmt.execute();
        }
    }

    /**
     * Delete resume record from database
     *
     * @param resumeId ID of the resume
     */
    public void deleteResume(int resumeId) throws SQLException {
        String sql = "{call candidate.sp_DeleteResume(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, resumeId);
            stmt.execute();
        }
    }

    /**
     * Get all resumes (for migration purposes)
     *
     * @return List of all resumes
     */
    public List<ResumeDTO> getAllResumes() throws SQLException {
        String sql = "{call candidate.sp_GetAllResumes()}";

        List<ResumeDTO> resumes = new ArrayList<>();

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ResumeDTO resume = new ResumeDTO();
                resume.setResumeId(rs.getInt("ResumeId"));
                resume.setCandidateId(rs.getInt("CandidateId"));
                resume.setDriveFileId(rs.getString("DriveFileId"));
                resume.setFileName(rs.getString("FileName"));
                resume.setFileUrl(rs.getString("FileUrl"));
                resume.setParsedJson(rs.getString("ParsedJson"));
                resume.setIsPrimary(rs.getBoolean("IsPrimary"));
                resume.setIsPublic(rs.getBoolean("IsPublic"));
                resume.setUploadedAt(rs.getTimestamp("UploadedAt"));
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
        String sql = "{call candidate.sp_SetPrimaryResume(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, resumeId);
            stmt.setInt(2, candidateId);

            stmt.execute();
        }
    }

    /**
     * Rename a resume file (update display name in database)
     *
     * @param resumeId ID of the resume
     * @param newFileName New display name for the file
     * @param candidateId Candidate ID for security verification
     * @return true if update was successful
     */
    public boolean renameResume(int resumeId, String newFileName, int candidateId) throws SQLException {
        String sql = "{call candidate.sp_RenameResume(?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, resumeId);
            stmt.setString(2, newFileName);
            stmt.setInt(3, candidateId);
            stmt.registerOutParameter(4, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(4);
        }
    }

    /**
     * Delete a resume by ID (with security check for candidate ownership)
     *
     * @param resumeId ID of the resume
     * @param candidateId Candidate ID for security verification
     * @return true if deletion was successful
     */
    public boolean deleteResumeSecure(int resumeId, int candidateId) throws SQLException {
        String sql = "{call candidate.sp_DeleteResumeSecure(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, resumeId);
            stmt.setInt(2, candidateId);
            stmt.registerOutParameter(3, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(3);
        }
    }
}
