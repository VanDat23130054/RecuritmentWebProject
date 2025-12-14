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

public class CompanyDAO {

    public List<Map<String, Object>> getTopEmployers(int limit) throws SQLException {
        List<Map<String, Object>> companies = new ArrayList<>();
        String sql = "{call employer.sp_GetTopEmployers(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> company = new HashMap<>();
                    company.put("companyId", rs.getInt("CompanyID"));
                    company.put("name", rs.getString("CompanyName"));
                    company.put("logoUrl", rs.getString("LogoUrl"));
                    company.put("cityName", rs.getString("CityName"));
                    company.put("activeJobCount", rs.getInt("ActiveJobCount"));
                    company.put("TopSkills", rs.getString("TopSkills"));
                    companies.add(company);
                }
            }
        }
        return companies;
    }

    public Map<String, Object> getCompanyDetail(Integer companyId) throws SQLException {
        String sql = "{call employer.sp_GetCompanyDetail(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> company = new HashMap<>();
                    company.put("companyId", rs.getInt("CompanyID"));
                    company.put("name", rs.getString("CompanyName"));
                    company.put("slug", rs.getString("Slug"));
                    company.put("logoUrl", rs.getString("LogoUrl"));
                    company.put("websiteUrl", rs.getString("WebsiteUrl"));
                    company.put("description", rs.getString("Description"));
                    company.put("industry", rs.getString("Industry"));
                    company.put("companySize", rs.getString("CompanySize"));
                    company.put("sizeRange", rs.getString("CompanySize"));
                    company.put("foundedYear", rs.getInt("FoundedYear"));
                    company.put("headquartersCityId", rs.getInt("HeadquartersCityId"));
                    company.put("address", rs.getString("Address"));
                    company.put("cityName", rs.getString("CityName"));
                    company.put("activeJobCount", rs.getInt("ActiveJobCount"));
                    return company;
                }
            }
        }
        return null;
    }

    public boolean updateCompanyProfile(Integer companyId, String name, String website,
            String description, String industry, String sizeRange,
            Integer foundedYear, Integer cityId, String logoUrl) throws SQLException {
        String sql = "{call employer.sp_UpdateCompanyProfile(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, companyId);
            stmt.setString(2, name);
            stmt.setString(3, website);
            stmt.setString(4, description);
            stmt.setString(5, industry);
            stmt.setString(6, sizeRange);

            if (foundedYear != null) {
                stmt.setInt(7, foundedYear);
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            if (cityId != null) {
                stmt.setInt(8, cityId);
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, logoUrl);

            // Use execute() instead of executeUpdate() to handle result sets
            stmt.execute();

            // For stored procedures that may return result sets, consume them
            while (stmt.getMoreResults() || stmt.getUpdateCount() != -1) {
                // Just consume the results
            }

            // If we got here without exception, the update was successful
            return true;
        }
    }
}
