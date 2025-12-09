package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.candidate.SavedJob;

public class SavedJobDAO {

    /**
     * Save a job for a user
     */
    public boolean saveJob(Integer userId, Integer jobId) throws SQLException {
        String sql = "{call candidate.sp_SaveJob(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, jobId);
            stmt.registerOutParameter(3, Types.INTEGER);

            stmt.execute();
            
            int savedJobId = stmt.getInt(3);
            return savedJobId > 0;
        }
    }

    /**
     * Unsave a job for a user
     */
    public boolean unsaveJob(Integer userId, Integer jobId) throws SQLException {
        String sql = "{call candidate.sp_UnsaveJob(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, jobId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Check if a job is saved by a user
     */
    public boolean isJobSaved(Integer userId, Integer jobId) throws SQLException {
        if (userId == null || jobId == null) {
            return false;
        }
        
        String sql = "{call candidate.sp_IsJobSaved(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, jobId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IsSaved") == 1;
                }
            }
        }
        return false;
    }

    /**
     * Get all saved jobs for a user
     */
    public List<SavedJob> getSavedJobsByUser(Integer userId) throws SQLException {
        List<SavedJob> savedJobs = new ArrayList<>();
        String sql = "{call candidate.sp_GetSavedJobsByUser(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SavedJob savedJob = new SavedJob();
                    savedJob.setSavedJobId(rs.getInt("SavedJobID"));
                    savedJob.setUserId(rs.getInt("UserID"));
                    savedJob.setJobId(rs.getInt("JobID"));
                    savedJob.setSavedAt(rs.getTimestamp("SavedAt").toLocalDateTime());
                    savedJobs.add(savedJob);
                }
            }
        }
        return savedJobs;
    }
}
