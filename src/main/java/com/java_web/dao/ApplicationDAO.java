package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.dto.ApplicationDetailDTO;
import com.java_web.model.dto.ApplicationFunnelDTO;
import com.java_web.model.dto.ApplicationListDTO;
import com.java_web.model.dto.ApplicationStatusCountsDTO;
import com.java_web.model.dto.TimelineDataDTO;

public class ApplicationDAO {

    /**
     * Get all applications for a recruiter's jobs with filters and pagination
     */
    public List<ApplicationListDTO> getApplicationsByRecruiter(Integer recruiterId, Integer jobId,
            String status, String keyword, int pageNumber, int pageSize) throws SQLException {
        List<ApplicationListDTO> applications = new ArrayList<>();
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
                    ApplicationListDTO app = new ApplicationListDTO();
                    app.setApplicationId(rs.getInt("ApplicationId"));
                    app.setJobId(rs.getInt("JobId"));
                    app.setJobTitle(rs.getString("JobTitle"));
                    app.setCandidateId(rs.getInt("CandidateId"));
                    app.setCandidateName(rs.getString("CandidateName"));
                    app.setCandidateEmail(rs.getString("CandidateEmail"));
                    app.setCoverLetter(rs.getString("CoverLetter"));
                    app.setSource(rs.getString("Source"));
                    app.setAppliedAt(rs.getTimestamp("AppliedAt"));
                    app.setStatus(rs.getString("Status"));
                    app.setResumeId((Integer) rs.getObject("ResumeId"));
                    app.setFileUrl(rs.getString("FileUrl"));
                    app.setFileName(rs.getString("FileName"));
                    app.setCompanyName(rs.getString("companyName"));
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
    public ApplicationDetailDTO getApplicationDetail(Integer applicationId, Integer recruiterId)
            throws SQLException {
        String sql = "{call employer.sp_GetApplicationDetail(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, applicationId);
            stmt.setInt(2, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ApplicationDetailDTO app = new ApplicationDetailDTO();
                    app.setApplicationId(rs.getInt("ApplicationId"));
                    app.setJobId(rs.getInt("JobId"));
                    app.setCandidateId(rs.getInt("CandidateId"));
                    app.setCandidateEmail(rs.getString("candidateEmail"));
                    app.setCandidateName(rs.getString("candidateName"));
                    app.setCandidateSummary(rs.getString("candidateSummary"));
                    app.setCandidateCity(rs.getString("candidateCity"));
                    app.setCoverLetter(rs.getString("CoverLetter"));
                    app.setSource(rs.getString("Source"));
                    app.setAppliedAt(rs.getTimestamp("AppliedAt"));
                    app.setStatus(rs.getString("Status"));
                    app.setResumeId((Integer) rs.getObject("ResumeId"));
                    app.setJobTitle(rs.getString("jobTitle"));
                    app.setJobDescription(rs.getString("jobDescription"));
                    app.setCompanyName(rs.getString("companyName"));
                    app.setResumeFileName(rs.getString("resumeFileName"));
                    app.setResumeFileUrl(rs.getString("resumeFileUrl"));
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
    public ApplicationStatusCountsDTO getApplicationStatusCounts(Integer recruiterId) throws SQLException {
        String sql = "{call employer.sp_GetApplicationStatusCounts(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ApplicationStatusCountsDTO counts = new ApplicationStatusCountsDTO();
                    counts.setAll(rs.getInt("allCount"));
                    counts.setApplied(rs.getInt("appliedCount"));
                    counts.setUnderReview(rs.getInt("underReviewCount"));
                    counts.setInterview(rs.getInt("interviewCount"));
                    counts.setRejected(rs.getInt("rejectedCount"));
                    return counts;
                }
            }
        }
        return null;
    }

    /**
     * Get applications timeline for the last N days
     */
    public List<TimelineDataDTO> getApplicationsTimeline(Integer recruiterId, int days) throws SQLException {
        List<TimelineDataDTO> timeline = new ArrayList<>();
        String sql = "{call employer.sp_GetApplicationsTimeline(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, recruiterId);
            stmt.setInt(2, days);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimelineDataDTO data = new TimelineDataDTO();
                    data.setDate(rs.getDate("Date"));
                    data.setCount(rs.getInt("ApplicationCount"));
                    timeline.add(data);
                }
            }
        }
        return timeline;
    }

    /**
     * Get application funnel conversion data
     */
    public ApplicationFunnelDTO getApplicationFunnel(Integer recruiterId) throws SQLException {
        String sql = "{call employer.sp_GetApplicationFunnel(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ApplicationFunnelDTO funnel = new ApplicationFunnelDTO();
                    funnel.setTotalViews(rs.getInt("TotalViews"));
                    funnel.setTotalApplications(rs.getInt("TotalApplications"));
                    funnel.setUnderReview(rs.getInt("UnderReview"));
                    funnel.setInterviewed(rs.getInt("Interviewed"));
                    funnel.setOffered(rs.getInt("Offered"));
                    funnel.setViewToAppRate(rs.getDouble("ViewToAppRate"));
                    funnel.setAppToReviewRate(rs.getDouble("AppToReviewRate"));
                    funnel.setReviewToInterviewRate(rs.getDouble("ReviewToInterviewRate"));
                    funnel.setInterviewToOfferRate(rs.getDouble("InterviewToOfferRate"));
                    return funnel;
                }
            }
        }
        return null;
    }

    /**
     * Get applications for a specific candidate with optional status and
     * pagination
     */
    public List<ApplicationListDTO> getApplicationsByCandidate(Integer candidateId, String status, int pageNumber, int pageSize) throws SQLException {
        List<ApplicationListDTO> applications = new ArrayList<>();
        String sql = "{call candidate.sp_GetApplicationsByCandidate(?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, candidateId);

            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(2, status);
            } else {
                stmt.setNull(2, Types.NVARCHAR);
            }

            stmt.setInt(3, pageNumber);
            stmt.setInt(4, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ApplicationListDTO app = new ApplicationListDTO();
                    app.setApplicationId(rs.getInt("ApplicationId"));
                    app.setJobId(rs.getInt("JobId"));
                    app.setJobTitle(rs.getString("JobTitle"));
                    app.setCandidateId(rs.getInt("CandidateId"));
                    app.setCandidateName(rs.getString("CandidateName"));
                    app.setCandidateEmail(rs.getString("CandidateEmail"));
                    app.setCompanyName(rs.getString("CompanyName"));
                    app.setCoverLetter(rs.getString("CoverLetter"));
                    app.setSource(rs.getString("Source"));
                    app.setAppliedAt(rs.getTimestamp("AppliedAt"));
                    app.setStatus(rs.getString("Status"));
                    app.setResumeId((Integer) rs.getObject("ResumeId"));
                    app.setFileUrl(rs.getString("FileUrl"));
                    app.setFileName(rs.getString("FileName"));
                    applications.add(app);
                }
            }
        }
        return applications;
    }

    /**
     * Get total application count for a candidate (for pagination)
     */
    public int getApplicationCountByCandidate(Integer candidateId, String status) throws SQLException {
        String sql = "{call candidate.sp_GetApplicationCountByCandidate(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, candidateId);

            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(2, status);
            } else {
                stmt.setNull(2, Types.NVARCHAR);
            }

            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();

            return stmt.getInt(3);
        }
    }

    /**
     * Allow candidate to withdraw an application. Only allow if the application
     * belongs to candidate.
     */
    public boolean withdrawApplication(Integer applicationId, Integer candidateId) throws SQLException {
        String sql = "{call candidate.sp_WithdrawApplication(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, candidateId);
            stmt.registerOutParameter(3, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(3);
        }
    }

    /**
     * Check if a candidate has already applied for a job
     */
    public boolean hasApplied(Integer candidateId, Integer jobId) throws SQLException {
        String sql = "{call candidate.sp_HasApplied(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, candidateId);
            stmt.setInt(2, jobId);
            stmt.registerOutParameter(3, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(3);
        }
    }

    /**
     * Get application detail for a candidate (only returns if application
     * belongs to candidate)
     */
    public ApplicationDetailDTO getApplicationDetailByCandidate(Integer applicationId, Integer candidateId) throws SQLException {
        String sql = "{call candidate.sp_GetApplicationDetailByCandidate(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, candidateId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ApplicationDetailDTO app = new ApplicationDetailDTO();
                    app.setApplicationId(rs.getInt("ApplicationId"));
                    app.setJobId(rs.getInt("JobId"));
                    app.setJobTitle(rs.getString("JobTitle"));
                    app.setCompanyName(rs.getString("CompanyName"));
                    app.setAppliedAt(rs.getTimestamp("AppliedAt"));
                    app.setStatus(rs.getString("Status"));
                    app.setCoverLetter(rs.getString("CoverLetter"));
                    app.setResumeFileName(rs.getString("ResumeFileName"));
                    app.setResumeFileUrl(rs.getString("ResumeFileUrl"));
                    // RecruiterNote - may not exist in all implementations
                    try {
                        app.setRecruiterNote(rs.getString("RecruiterNote"));
                    } catch (SQLException e) {
                        // Column might not exist, ignore
                    }
                    return app;
                }
            }
        }
        return null;
    }

    /**
     * Update application cover letter (only if application belongs to candidate
     * and status is 'Applied')
     */
    public boolean updateApplicationCoverLetter(Integer applicationId, Integer candidateId, String coverLetter) throws SQLException {
        String sql = "{call candidate.sp_UpdateApplicationCoverLetter(?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, candidateId);
            stmt.setString(3, coverLetter);
            stmt.registerOutParameter(4, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(4);
        }
    }
}
