package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.dto.ApplicationStatusStatDTO;
import com.java_web.model.dto.RecentApplicationDTO;
import com.java_web.model.dto.RecruiterDashboardStatsDTO;
import com.java_web.model.dto.RecruiterJobDTO;
import com.java_web.model.employer.Recruiter;

public class RecruiterDAO {

    /**
     * Get recruiter profile by user ID
     */
    public Recruiter getRecruiterByUserId(Integer userId) throws SQLException {
        String sql = "{call employer.sp_GetRecruiterByUserId(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

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
    public RecruiterDashboardStatsDTO getDashboardStats(Integer recruiterId) throws SQLException {
        String sql = "{call employer.sp_GetRecruiterDashboardStats(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RecruiterDashboardStatsDTO stats = new RecruiterDashboardStatsDTO();
                    stats.setTotalJobs(rs.getInt("TotalJobs"));
                    stats.setActiveJobs(rs.getInt("ActiveJobs"));
                    stats.setTotalApplications(rs.getInt("TotalApplications"));
                    stats.setNewApplications(rs.getInt("NewApplications"));
                    stats.setInterviewsScheduled(rs.getInt("InterviewsScheduled"));
                    stats.setTotalViews(rs.getInt("TotalViews"));
                    return stats;
                }
            }
        }
        return null;
    }

    /**
     * Get recent applications for recruiter's jobs
     */
    public List<RecentApplicationDTO> getRecentApplications(Integer recruiterId, int limit) throws SQLException {
        List<RecentApplicationDTO> applications = new ArrayList<>();
        String sql = "{call employer.sp_GetRecentApplicationsByRecruiter(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RecentApplicationDTO app = new RecentApplicationDTO();
                    app.setApplicationId(rs.getInt("ApplicationId"));
                    app.setJobId(rs.getInt("JobId"));
                    app.setJobTitle(rs.getString("JobTitle"));
                    app.setCandidateId(rs.getInt("CandidateId"));
                    app.setCandidateName(rs.getString("CandidateName"));
                    app.setCandidateEmail(rs.getString("CandidateEmail"));

                    app.setAppliedAt(rs.getTimestamp("AppliedAt"));
                    app.setResumeId(rs.getInt("ResumeId"));

                    app.setStatus(rs.getString("Status"));
                    app.setFileUrl(rs.getString("FileUrl"));
                    applications.add(app);
                }
            }
        }
        return applications;
    }

    /**
     * Get jobs posted by recruiter
     */
    public List<RecruiterJobDTO> getRecruiterJobs(Integer recruiterId, int pageNumber, int pageSize) throws SQLException {
        List<RecruiterJobDTO> jobs = new ArrayList<>();
        String sql = "{call employer.sp_GetRecruiterJobs(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);
            stmt.setNull(2, Types.TINYINT); // statusId - NULL to get all statuses
            stmt.setNull(3, Types.NVARCHAR); // keyword - NULL for no filtering
            stmt.setInt(4, pageNumber);
            stmt.setInt(5, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RecruiterJobDTO job = new RecruiterJobDTO();
                    job.setJobId(rs.getInt("JobId"));
                    job.setTitle(rs.getString("Title"));
                    job.setSlug(rs.getString("Slug"));
                    job.setStatus(rs.getString("Status"));
                    job.setStatusId(rs.getByte("StatusId"));
                    job.setPostedAt(rs.getTimestamp("PostedAt"));
                    job.setExpiresAt(rs.getTimestamp("ExpiresAt"));
                    job.setViewsCount(rs.getInt("ViewsCount"));
                    job.setApplicationsCount(rs.getInt("ApplicationsCount"));
                    job.setIsFeatured(rs.getBoolean("IsFeatured"));
                    job.setCityName(rs.getString("CityName"));
                    job.setEmploymentType(rs.getString("EmploymentType"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    /**
     * Get application statistics by status
     */
    public List<ApplicationStatusStatDTO> getApplicationStatsByStatus(Integer recruiterId) throws SQLException {
        List<ApplicationStatusStatDTO> stats = new ArrayList<>();
        String sql = "{call employer.sp_GetApplicationStatsByStatus(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationStatusStatDTO stat = new ApplicationStatusStatDTO();
                    stat.setStatus(rs.getString("Status"));
                    stat.setCount(rs.getInt("Count"));
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

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

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
