package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.dto.JobDetailDTO;
import com.java_web.model.dto.JobForEditDTO;
import com.java_web.model.dto.JobPerformanceDTO;
import com.java_web.model.dto.JobSearchDTO;
import com.java_web.model.dto.JobSkillDTO;
import com.java_web.model.dto.RecruiterJobDTO;
import com.java_web.model.dto.RelatedJobDTO;

public class JobDAO {

    public int getTotalPublishedJobCount() throws SQLException {
        String sql = "{call employer.sp_GetTotalPublishedJobCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }

    public List<JobSearchDTO> searchJobs(String keyword, Integer cityId,
            Integer skillId, int pageNumber,
            int pageSize) throws SQLException {
        List<JobSearchDTO> jobs = new ArrayList<>();
        String sql = "{call employer.sp_SearchJobs(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, keyword);
            if (cityId != null) {
                stmt.setInt(2, cityId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (skillId != null) {
                stmt.setInt(3, skillId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, pageNumber);
            stmt.setInt(5, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JobSearchDTO job = new JobSearchDTO();
                    job.setJobId(rs.getInt("JobID"));
                    job.setTitle(rs.getString("Title"));
                    job.setSlug(rs.getString("Slug"));
                    job.setCompanyId(rs.getInt("CompanyID"));
                    job.setCompanyName(rs.getString("CompanyName"));
                    job.setLogoUrl(rs.getString("LogoUrl"));
                    job.setCityName(rs.getString("CityName"));
                    job.setSalaryMin(rs.getBigDecimal("SalaryMin"));
                    job.setSalaryMax(rs.getBigDecimal("SalaryMax"));
                    job.setCurrency(rs.getString("Currency"));
                    job.setIsFeatured(rs.getBoolean("IsFeatured"));
                    job.setSkills(rs.getString("Skills"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    public JobDetailDTO getJobDetail(Integer jobId) throws SQLException {
        String sql = "{call employer.sp_GetJobDetail(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JobDetailDTO job = new JobDetailDTO();
                    job.setJobId(rs.getInt("JobID"));
                    job.setTitle(rs.getString("Title"));
                    job.setSlug(rs.getString("Slug"));
                    job.setCompanyId(rs.getInt("CompanyID"));
                    job.setCompanyName(rs.getString("CompanyName"));
                    job.setLogoUrl(rs.getString("LogoUrl"));
                    job.setCityName(rs.getString("CityName"));
                    job.setSalaryMin(rs.getBigDecimal("SalaryMin"));
                    job.setSalaryMax(rs.getBigDecimal("SalaryMax"));
                    job.setCurrency(rs.getString("Currency"));
                    job.setDescription(rs.getString("Description"));
                    job.setRequirements(rs.getString("Requirements"));
                    job.setBenefits(rs.getString("Benefits"));
                    job.setEmploymentType(rs.getString("EmploymentType"));
                    job.setSeniorityLevel(rs.getString("SeniorityLevel"));
                    job.setRemoteType(rs.getString("RemoteType"));
                    job.setIsFeatured(rs.getBoolean("IsFeatured"));

                    job.setExpiresAt(rs.getTimestamp("ExpiresAt"));
                    job.setPostedAt(rs.getTimestamp("PostedAt"));

                    job.setSkills(rs.getString("Skills"));
                    return job;
                }
            }
        }
        return null;
    }

    public List<RelatedJobDTO> getRelatedJobs(Integer jobId, Integer companyId, int limit)
            throws SQLException {
        List<RelatedJobDTO> jobs = new ArrayList<>();
        String sql = "{call employer.sp_GetRelatedJobs(?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, companyId);
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RelatedJobDTO job = new RelatedJobDTO();
                    job.setJobId(rs.getInt("JobID"));
                    job.setTitle(rs.getString("Title"));
                    job.setSlug(rs.getString("Slug"));
                    job.setCompanyName(rs.getString("CompanyName"));
                    job.setLogoUrl(rs.getString("LogoUrl"));
                    job.setCityName(rs.getString("CityName"));
                    job.setSalaryMin(rs.getBigDecimal("SalaryMin"));
                    job.setSalaryMax(rs.getBigDecimal("SalaryMax"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    /**
     * Get all jobs for a recruiter with pagination and filtering
     */
    public List<RecruiterJobDTO> getRecruiterJobs(Integer recruiterId, Integer statusId,
            String keyword, int pageNumber, int pageSize) throws SQLException {
        List<RecruiterJobDTO> jobs = new ArrayList<>();
        String sql = "{call employer.sp_GetRecruiterJobs(?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            if (statusId != null) {
                stmt.setInt(2, statusId);
            } else {
                stmt.setNull(2, Types.TINYINT);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(3, keyword);
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.setInt(4, pageNumber);
            stmt.setInt(5, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RecruiterJobDTO job = new RecruiterJobDTO();
                    job.setJobId(rs.getInt("JobID"));
                    job.setTitle(rs.getString("Title"));
                    job.setSlug(rs.getString("Slug"));
                    job.setCityName(rs.getString("CityName"));
                    job.setStatusId(rs.getByte("StatusId"));
                    job.setStatus(rs.getString("Status"));

                    job.setPostedAt(rs.getTimestamp("PostedAt"));
                    job.setExpiresAt(rs.getTimestamp("ExpiresAt"));

                    job.setViewsCount(rs.getInt("ViewsCount"));
                    job.setApplicationsCount(rs.getInt("ApplicationsCount"));
                    job.setIsFeatured(rs.getBoolean("IsFeatured"));
                    job.setEmploymentType(rs.getString("EmploymentType"));
                    job.setSalaryMin(rs.getBigDecimal("SalaryMin"));
                    job.setSalaryMax(rs.getBigDecimal("SalaryMax"));
                    job.setCurrency(rs.getString("Currency"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    /**
     * Get total job count for a recruiter with optional filters
     */
    public int getRecruiterJobCount(Integer recruiterId, Integer statusId, String keyword)
            throws SQLException {
        String sql = "{call employer.sp_GetRecruiterJobCount(?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, recruiterId);

            if (statusId != null) {
                stmt.setInt(2, statusId);
            } else {
                stmt.setNull(2, Types.TINYINT);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(3, keyword);
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.registerOutParameter(4, Types.INTEGER);
            stmt.execute();

            return stmt.getInt(4);
        }
    }

    /**
     * Create a new job posting
     */
    public Integer createJob(Integer companyId, Integer recruiterId, String title, String description,
            String requirements, String benefits, Integer cityId,
            Integer employmentType, Integer seniorityLevel, Integer remoteType,
            Integer salaryMin, Integer salaryMax, String currency,
            String expiresAt, Byte statusId) throws SQLException {
        String sql = "{call employer.sp_CreateJob(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, companyId);
            stmt.setInt(2, recruiterId);
            stmt.setString(3, title);
            stmt.setString(4, description);

            if (requirements != null) {
                stmt.setString(5, requirements);
            } else {
                stmt.setNull(5, Types.NVARCHAR);
            }

            if (benefits != null) {
                stmt.setString(6, benefits);
            } else {
                stmt.setNull(6, Types.NVARCHAR);
            }

            stmt.setInt(7, cityId);
            stmt.setInt(8, employmentType);

            if (seniorityLevel != null) {
                stmt.setInt(9, seniorityLevel);
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            if (remoteType != null) {
                stmt.setInt(10, remoteType);
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            if (salaryMin != null) {
                stmt.setInt(11, salaryMin);
            } else {
                stmt.setNull(11, Types.INTEGER);
            }

            if (salaryMax != null) {
                stmt.setInt(12, salaryMax);
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            if (currency != null) {
                stmt.setString(13, currency);
            } else {
                stmt.setString(13, "USD");
            }

            if (expiresAt != null && !expiresAt.isEmpty()) {
                stmt.setString(14, expiresAt);
            } else {
                stmt.setNull(14, Types.VARCHAR);
            }

            stmt.setByte(15, statusId);
            stmt.registerOutParameter(16, Types.INTEGER);

            stmt.execute();
            return stmt.getInt(16);
        }
    }

    /**
     * Add a skill to a job
     */
    public void addJobSkill(Integer jobId, Integer skillId) throws SQLException {
        String sql = "{call employer.sp_AddJobSkill(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, skillId);
            stmt.execute();
        }
    }

    /**
     * Get job details for editing (with authorization check)
     */
    public JobForEditDTO getJobForEdit(Integer jobId, Integer recruiterId) throws SQLException {
        String sql = "{call employer.sp_GetJobForEdit(?, ?)}";
        JobForEditDTO result = new JobForEditDTO();

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, recruiterId);

            // First result set: job details
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result.setJobId(rs.getInt("JobId"));
                    result.setCompanyId(rs.getInt("CompanyId"));
                    result.setRecruiterId(rs.getInt("RecruiterId"));
                    result.setTitle(rs.getString("Title"));
                    result.setSlug(rs.getString("Slug"));
                    result.setDescription(rs.getString("Description"));
                    result.setRequirements(rs.getString("Requirements"));
                    result.setBenefits(rs.getString("Benefits"));
                    result.setCityId(rs.getInt("CityId"));
                    result.setEmploymentTypeId(rs.getByte("EmploymentTypeId"));

                    Object seniorityLevel = rs.getObject("SeniorityLevelId");
                    result.setSeniorityLevelId(seniorityLevel != null ? rs.getByte("SeniorityLevelId") : null);

                    Object remoteType = rs.getObject("RemoteTypeId");
                    result.setRemoteTypeId(remoteType != null ? rs.getByte("RemoteTypeId") : null);

                    result.setSalaryMin(rs.getBigDecimal("SalaryMin"));
                    result.setSalaryMax(rs.getBigDecimal("SalaryMax"));
                    result.setCurrency(rs.getString("Currency"));
                    result.setStatusId(rs.getByte("StatusId"));
                    result.setIsFeatured(rs.getBoolean("IsFeatured"));

                    result.setPostedAt(rs.getTimestamp("PostedAt"));
                    result.setExpiresAt(rs.getTimestamp("ExpiresAt"));

                    result.setViewsCount(rs.getInt("ViewsCount"));
                    result.setApplicationsCount(rs.getInt("ApplicationsCount"));
                } else {
                    return null;
                }
            }

            // Second result set: skills
            if (stmt.getMoreResults()) {
                List<JobSkillDTO> skills = new ArrayList<>();
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        JobSkillDTO skill = new JobSkillDTO();
                        skill.setSkillId(rs.getInt("SkillId"));
                        skill.setSkillName(rs.getString("SkillName"));
                        skills.add(skill);
                    }
                }
                result.setSkills(skills);
            }
        }

        return result;
    }

    /**
     * Update an existing job posting
     */
    public boolean updateJob(Integer jobId, Integer recruiterId, String title, String description,
            String requirements, String benefits, Integer cityId,
            Integer employmentType, Integer seniorityLevel, Integer remoteType,
            Double salaryMin, Double salaryMax, String currency,
            String expiresAt, Byte statusId) throws SQLException {
        String sql = "{call employer.sp_UpdateJob(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, recruiterId);
            stmt.setString(3, title);
            stmt.setString(4, description);

            if (requirements != null) {
                stmt.setString(5, requirements);
            } else {
                stmt.setNull(5, Types.NVARCHAR);
            }

            if (benefits != null) {
                stmt.setString(6, benefits);
            } else {
                stmt.setNull(6, Types.NVARCHAR);
            }

            stmt.setInt(7, cityId);
            stmt.setByte(8, employmentType.byteValue());

            if (seniorityLevel != null) {
                stmt.setByte(9, seniorityLevel.byteValue());
            } else {
                stmt.setNull(9, Types.TINYINT);
            }

            if (remoteType != null) {
                stmt.setByte(10, remoteType.byteValue());
            } else {
                stmt.setNull(10, Types.TINYINT);
            }

            if (salaryMin != null) {
                stmt.setDouble(11, salaryMin);
            } else {
                stmt.setNull(11, Types.INTEGER);
            }

            if (salaryMax != null) {
                stmt.setDouble(12, salaryMax);
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            if (currency != null) {
                stmt.setString(13, currency);
            } else {
                stmt.setString(13, "USD");
            }

            if (expiresAt != null && !expiresAt.isEmpty()) {
                stmt.setString(14, expiresAt);
            } else {
                stmt.setNull(14, Types.VARCHAR);
            }

            stmt.setByte(15, statusId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Success") == 1;
                }
            }
        }

        return false;
    }

    /**
     * Remove all skills from a job (used before adding new ones during update)
     */
    public void removeAllJobSkills(Integer jobId) throws SQLException {
        String sql = "{call employer.sp_RemoveAllJobSkills(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.execute();
        }
    }

    /**
     * Delete/deactivate a job
     */
    public boolean deleteJob(Integer jobId, Integer recruiterId) throws SQLException {
        String sql = "{call employer.sp_DeleteJob(?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Success") == 1;
                }
            }
        }

        return false;
    }

    /**
     * Get job performance metrics for graphs
     */
    public List<JobPerformanceDTO> getJobPerformanceMetrics(Integer recruiterId) throws SQLException {
        List<JobPerformanceDTO> metrics = new ArrayList<>();
        String sql = "{call employer.sp_GetJobPerformanceMetrics(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, recruiterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JobPerformanceDTO job = new JobPerformanceDTO();
                    job.setJobId(rs.getInt("JobID"));
                    job.setTitle(rs.getString("Title"));
                    job.setViewsCount(rs.getInt("ViewsCount"));
                    job.setApplicationsCount(rs.getInt("ApplicationsCount"));
                    job.setConversionRate(rs.getDouble("ConversionRate"));
                    job.setDaysSincePosted(rs.getInt("DaysSincePosted"));
                    metrics.add(job);
                }
            }
        }
        return metrics;
    }
}
