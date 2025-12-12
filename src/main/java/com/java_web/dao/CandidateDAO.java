package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.java_web.config.DB;
import com.java_web.model.candidate.Candidate;

public class CandidateDAO {

    public Candidate getCandidateByUserId(Integer userId) throws SQLException {
        String sql = "{call candidate.sp_GetCandidateByUserId(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidate c = new Candidate();
                    c.setCandidateId(rs.getInt("CandidateID"));
                    c.setUserId(rs.getInt("UserID"));
                    c.setFullName(rs.getString("FullName"));
                    c.setHeadline(rs.getString("Headline"));
                    c.setSummary(rs.getString("Summary"));
                    c.setYearsOfExperience(rs.getObject("YearsOfExperience") != null ? rs.getDouble("YearsOfExperience") : null);
                    c.setCityId(rs.getObject("CityID") != null ? rs.getInt("CityID") : null);
                    c.setCountryId(rs.getObject("CountryID") != null ? rs.getInt("CountryID") : null);
                    c.setAvatarUrl(rs.getString("AvatarUrl"));
                    c.setPublicProfile(rs.getBoolean("IsPublicProfile"));

                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    if (createdAt != null) c.setCreatedAt(createdAt.toLocalDateTime());

                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    if (updatedAt != null) c.setUpdatedAt(updatedAt.toLocalDateTime());

                    return c;
                }
            }
        }
        return null;
    }
}
