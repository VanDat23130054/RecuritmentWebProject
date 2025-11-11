package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
