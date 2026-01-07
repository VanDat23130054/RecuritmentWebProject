package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.java_web.config.DB;
import com.java_web.model.candidate.Candidate;

public class CandidateDAO {

    /**
     * Get candidate by User ID using stored procedure
     */
    public Candidate getCandidateByUserId(Integer userId) throws SQLException {
        String sql = "{call candidate.sp_GetCandidateByUserId(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidate c = new Candidate();
                    c.setCandidateId(rs.getInt("CandidateId"));
                    c.setUserId(rs.getInt("UserId"));
                    c.setFullName(rs.getString("FullName"));
                    c.setHeadline(rs.getString("Headline"));
                    c.setSummary(rs.getString("Summary"));
                    c.setYearsOfExperience(rs.getObject("YearsOfExperience") != null ? rs.getDouble("YearsOfExperience") : null);
                    c.setCityId(rs.getObject("CityId") != null ? rs.getInt("CityId") : null);
                    c.setCountryId(rs.getObject("CountryId") != null ? rs.getInt("CountryId") : null);
                    c.setAvatarUrl(rs.getString("AvatarUrl"));
                    c.setPublicProfile(rs.getBoolean("PublicProfile"));
                    c.setCityName(rs.getString("CityName"));
                    c.setCountryName(rs.getString("CountryName"));

                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        c.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    if (updatedAt != null) {
                        c.setUpdatedAt(updatedAt.toLocalDateTime());
                    }

                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Update candidate profile by User ID using stored procedure
     */
    public boolean updateCandidateByUserId(Integer userId, Candidate candidate) throws SQLException {
        String sql = "{call candidate.sp_UpdateCandidateProfile(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, candidate.getFullName());
            stmt.setString(3, candidate.getHeadline());
            stmt.setString(4, candidate.getSummary());

            if (candidate.getYearsOfExperience() != null) {
                stmt.setDouble(5, candidate.getYearsOfExperience());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            if (candidate.getCityId() != null) {
                stmt.setInt(6, candidate.getCityId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (candidate.getCountryId() != null) {
                stmt.setInt(7, candidate.getCountryId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setString(8, candidate.getAvatarUrl());
            stmt.setBoolean(9, candidate.isPublicProfile());
            stmt.registerOutParameter(10, Types.BIT);

            stmt.execute();

            return stmt.getBoolean(10);
        }
    }

}
