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
}
