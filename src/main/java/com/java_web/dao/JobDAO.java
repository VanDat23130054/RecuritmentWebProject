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

public class JobDAO {

    public int getTotalPublishedJobCount() throws SQLException {
        String sql = "{call employer.sp_GetTotalPublishedJobCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }

    public List<Map<String, Object>> searchJobs(String keyword, Integer cityId,
            Integer skillId, int pageNumber,
            int pageSize) throws SQLException {
        List<Map<String, Object>> jobs = new ArrayList<>();
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
                    Map<String, Object> job = new HashMap<>();
                    job.put("jobId", rs.getInt("JobID"));
                    job.put("title", rs.getString("Title"));
                    job.put("slug", rs.getString("Slug"));
                    job.put("companyId", rs.getInt("CompanyID"));
                    job.put("companyName", rs.getString("CompanyName"));
                    job.put("logoUrl", rs.getString("LogoUrl"));
                    job.put("cityName", rs.getString("CityName"));
                    job.put("salaryMin", rs.getObject("SalaryMin"));
                    job.put("salaryMax", rs.getObject("SalaryMax"));
                    job.put("currency", rs.getString("Currency"));
                    job.put("isFeatured", rs.getBoolean("IsFeatured"));
                    job.put("Skills", rs.getString("Skills"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    public Map<String, Object> getJobDetail(Integer jobId) throws SQLException {
        String sql = "{call employer.sp_GetJobDetail(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> job = new HashMap<>();
                    job.put("jobId", rs.getInt("JobID"));
                    job.put("title", rs.getString("Title"));
                    job.put("slug", rs.getString("Slug"));
                    job.put("companyId", rs.getInt("CompanyID"));
                    job.put("companyName", rs.getString("CompanyName"));
                    job.put("logoUrl", rs.getString("LogoUrl"));
                    job.put("cityName", rs.getString("CityName"));
                    job.put("salaryMin", rs.getObject("SalaryMin"));
                    job.put("salaryMax", rs.getObject("SalaryMax"));
                    job.put("currency", rs.getString("Currency"));
                    job.put("description", rs.getString("Description"));
                    job.put("requirements", rs.getString("Requirements"));
                    job.put("benefits", rs.getString("Benefits"));
                    job.put("employmentType", rs.getString("EmploymentType"));
                    job.put("seniorityLevel", rs.getString("SeniorityLevel"));
                    job.put("remoteType", rs.getString("RemoteType"));
                    job.put("isFeatured", rs.getBoolean("IsFeatured"));
                    job.put("expiresAt", rs.getTimestamp("ExpiresAt"));
                    job.put("postedAt", rs.getTimestamp("PostedAt"));
                    job.put("skills", rs.getString("Skills"));
                    return job;
                }
            }
        }
        return null;
    }

    public List<Map<String, Object>> getRelatedJobs(Integer jobId, Integer companyId, int limit) 
            throws SQLException {
        List<Map<String, Object>> jobs = new ArrayList<>();
        String sql = "{call employer.sp_GetRelatedJobs(?, ?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, jobId);
            stmt.setInt(2, companyId);
            stmt.setInt(3, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> job = new HashMap<>();
                    job.put("jobId", rs.getInt("JobID"));
                    job.put("title", rs.getString("Title"));
                    job.put("slug", rs.getString("Slug"));
                    job.put("companyName", rs.getString("CompanyName"));
                    job.put("logoUrl", rs.getString("LogoUrl"));
                    job.put("cityName", rs.getString("CityName"));
                    job.put("salaryMin", rs.getObject("SalaryMin"));
                    job.put("salaryMax", rs.getObject("SalaryMax"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    /**
     * Create a new job posting
     */
    public Integer createJob(Integer companyId, String title, String description,
                            String requirements, String benefits, Integer cityId,
                            Integer employmentType, Integer seniorityLevel, Integer remoteType,
                            Integer salaryMin, Integer salaryMax, String currency,
                            String expiresAt, Byte statusId) throws SQLException {
        String sql = "{call employer.sp_CreateJob(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, companyId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            
            if (requirements != null) {
                stmt.setString(4, requirements);
            } else {
                stmt.setNull(4, Types.NVARCHAR);
            }
            
            if (benefits != null) {
                stmt.setString(5, benefits);
            } else {
                stmt.setNull(5, Types.NVARCHAR);
            }
            
            stmt.setInt(6, cityId);
            stmt.setInt(7, employmentType);
            
            if (seniorityLevel != null) {
                stmt.setInt(8, seniorityLevel);
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            if (remoteType != null) {
                stmt.setInt(9, remoteType);
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            
            if (salaryMin != null) {
                stmt.setInt(10, salaryMin);
            } else {
                stmt.setNull(10, Types.INTEGER);
            }
            
            if (salaryMax != null) {
                stmt.setInt(11, salaryMax);
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            
            if (currency != null) {
                stmt.setString(12, currency);
            } else {
                stmt.setString(12, "USD");
            }
            
            if (expiresAt != null && !expiresAt.isEmpty()) {
                stmt.setString(13, expiresAt);
            } else {
                stmt.setNull(13, Types.VARCHAR);
            }
            
            stmt.setByte(14, statusId);
            stmt.registerOutParameter(15, Types.INTEGER);
            
            stmt.execute();
            return stmt.getInt(15);
        }
    }

    /**
     * Add a skill to a job
     */
    public void addJobSkill(Integer jobId, Integer skillId) throws SQLException {
        String sql = "{call employer.sp_AddJobSkill(?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, jobId);
            stmt.setInt(2, skillId);
            stmt.execute();
        }
    }
}

