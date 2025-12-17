package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.dto.CompanyDetailDTO;
import com.java_web.model.dto.CompanyListDTO;

public class CompanyDAO {

    public List<CompanyListDTO> getTopEmployers(int limit) throws SQLException {
        List<CompanyListDTO> companies = new ArrayList<>();
        String sql = "{call employer.sp_GetTopEmployers(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CompanyListDTO company = new CompanyListDTO();
                    company.setCompanyId(rs.getInt("CompanyID"));
                    company.setName(rs.getString("CompanyName"));
                    company.setLogoUrl(rs.getString("LogoUrl"));
                    company.setCityName(rs.getString("CityName"));
                    company.setActiveJobCount(rs.getInt("ActiveJobCount"));
                    company.setTopSkills(rs.getString("TopSkills"));
                    companies.add(company);
                }
            }
        }
        return companies;
    }

    public CompanyDetailDTO getCompanyDetail(Integer companyId) throws SQLException {
        String sql = "{call employer.sp_GetCompanyDetail(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, companyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CompanyDetailDTO company = new CompanyDetailDTO();
                    company.setCompanyId(rs.getInt("CompanyID"));
                    company.setName(rs.getString("CompanyName"));
                    company.setSlug(rs.getString("Slug"));
                    company.setLogoUrl(rs.getString("LogoUrl"));
                    company.setWebsiteUrl(rs.getString("WebsiteUrl"));
                    company.setDescription(rs.getString("Description"));
                    company.setIndustry(rs.getString("Industry"));
                    company.setSizeRange(rs.getString("CompanySize"));
                    company.setFoundedYear(rs.getInt("FoundedYear"));
                    company.setHeadquartersCityId(rs.getInt("HeadquartersCityId"));
                    company.setAddress(rs.getString("Address"));
                    company.setCityName(rs.getString("CityName"));
                    company.setActiveJobCount(rs.getInt("ActiveJobCount"));
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
