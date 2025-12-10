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
import com.java_web.model.employer.Recruiter;

public class RecruiterDAO {

    /**
     * Get recruiter profile by user ID
     */
    public Recruiter getRecruiterByUserId(Integer userId) throws SQLException {
        String sql = "{call employer.sp_GetRecruiterByUserId(?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recruiter recruiter = new Recruiter();
                    recruiter.setRecruiterId(rs.getInt("RecruiterId"));
                    recruiter.setUserId(rs.getInt("UserId"));
                    recruiter.setCompanyId(rs.getInt("CompanyId"));
                    recruiter.setTitle(rs.getString("Title"));
                    recruiter.setPrimaryContact(rs.getBoolean("IsPrimaryContact"));
                    return recruiter;
                }
            }
        }
        return null;
    }

    /**
     * Get dashboard statistics for recruiter
     */
    public Map<String, Object> getDashboardStats(Integer recruiterId) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "{call employer.sp_GetRecruiterDashboardStats(?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalJobs", rs.getInt("TotalJobs"));
                    stats.put("activeJobs", rs.getInt("ActiveJobs"));
                    stats.put("totalApplications", rs.getInt("TotalApplications"));
                    stats.put("newApplications", rs.getInt("NewApplications"));
                    stats.put("interviewsScheduled", rs.getInt("InterviewsScheduled"));
                    stats.put("totalViews", rs.getInt("TotalViews"));
                }
            }
        }
        return stats;
    }

    /**
     * Get jobs posted by recruiter
     */
    public List<Map<String, Object>> getRecruiterJobs(Integer recruiterId, int pageNumber, int pageSize) throws SQLException {
        List<Map<String, Object>> jobs = new ArrayList<>();
        String sql = "{call employer.sp_GetRecruiterJobs(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);
            stmt.setInt(2, pageNumber);
            stmt.setInt(3, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> job = new HashMap<>();
                    job.put("jobId", rs.getInt("JobId"));
                    job.put("title", rs.getString("Title"));
                    job.put("slug", rs.getString("Slug"));
                    job.put("status", rs.getString("Status"));
                    job.put("statusId", rs.getByte("StatusId"));
                    job.put("postedAt", rs.getTimestamp("PostedAt"));
                    job.put("expiresAt", rs.getTimestamp("ExpiresAt"));
                    job.put("viewsCount", rs.getInt("ViewsCount"));
                    job.put("applicationsCount", rs.getInt("ApplicationsCount"));
                    job.put("isFeatured", rs.getBoolean("IsFeatured"));
                    job.put("cityName", rs.getString("CityName"));
                    job.put("employmentType", rs.getString("EmploymentType"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    /**
     * Get recent applications for recruiter's jobs
     */
    public List<Map<String, Object>> getRecentApplications(Integer recruiterId, int limit) throws SQLException {
        List<Map<String, Object>> applications = new ArrayList<>();
        String sql = "{call employer.sp_GetRecentApplicationsByRecruiter(?, ?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> app = new HashMap<>();
                    app.put("applicationId", rs.getInt("ApplicationId"));
                    app.put("jobId", rs.getInt("JobId"));
                    app.put("jobTitle", rs.getString("JobTitle"));
                    app.put("candidateId", rs.getInt("CandidateId"));
                    app.put("candidateName", rs.getString("CandidateName"));
                    app.put("candidateEmail", rs.getString("CandidateEmail"));
                    app.put("appliedAt", rs.getTimestamp("AppliedAt"));
                    app.put("status", rs.getString("Status"));
                    app.put("fileUrl", rs.getString("FileUrl"));
                    applications.add(app);
                }
            }
        }
        return applications;
    }

    /**
     * Get application statistics by status
     */
    public List<Map<String, Object>> getApplicationStatsByStatus(Integer recruiterId) throws SQLException {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "{call employer.sp_GetApplicationStatsByStatus(?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("status", rs.getString("Status"));
                    stat.put("count", rs.getInt("Count"));
                    stats.add(stat);
                }
            }
        }
        return stats;
    }

    /**
     * Create or update recruiter profile
     */
    public Integer createRecruiter(Recruiter recruiter) throws SQLException {
        String sql = "{call employer.sp_CreateRecruiter(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiter.getUserId());
            stmt.setInt(2, recruiter.getCompanyId());
            stmt.setString(3, recruiter.getTitle());
            stmt.setBoolean(4, recruiter.isPrimaryContact());
            stmt.registerOutParameter(5, Types.INTEGER);

            stmt.execute();
            return stmt.getInt(5);
        }
    }
}
