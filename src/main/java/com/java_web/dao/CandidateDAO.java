package com.java_web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.java_web.config.DB;
import com.java_web.model.candidate.Candidate;

public class CandidateDAO {

    public Candidate getCandidateByUserId(Integer userId) throws SQLException {
        String sql = "SELECT c.CandidateId, c.UserId, c.FullName, c.Headline, c.Summary, c.YearsOfExperience, "
                + "c.CityId, c.CountryId, c.AvatarUrl, c.PublicProfile, c.CreatedAt, c.UpdatedAt, "
                + "ISNULL(ci.Name, '') AS CityName, ISNULL(co.Name, '') AS CountryName "
                + "FROM candidate.Candidates c "
                + "LEFT JOIN common.Cities ci ON c.CityId = ci.CityId "
                + "LEFT JOIN common.Countries co ON c.CountryId = co.CountryId "
                + "WHERE c.UserId = ?";

        try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public boolean updateResumeUrlByUserId(Integer userId, String resumeUrl) throws SQLException {
        String sql = "UPDATE candidate.Candidates SET ResumeUrl = ? WHERE UserID = ?";
        try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resumeUrl);
            ps.setInt(2, userId);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    // New method to update candidate profile fields by UserID
    public boolean updateCandidateByUserId(Integer userId, Candidate candidate) throws SQLException {
        String sql = "UPDATE candidate.Candidates SET FullName = ?, Headline = ?, Summary = ?, YearsOfExperience = ?, CityID = ?, CountryID = ?, AvatarUrl = ?, PublicProfile = ?, UpdatedAt = ? WHERE UserID = ?";

        try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, candidate.getFullName());
            ps.setString(2, candidate.getHeadline());
            ps.setString(3, candidate.getSummary());

            if (candidate.getYearsOfExperience() != null) {
                ps.setDouble(4, candidate.getYearsOfExperience());
            } else {
                ps.setNull(4, java.sql.Types.DOUBLE);
            }

            if (candidate.getCityId() != null) {
                ps.setInt(5, candidate.getCityId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (candidate.getCountryId() != null) {
                ps.setInt(6, candidate.getCountryId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, candidate.getAvatarUrl());
            ps.setBoolean(8, candidate.isPublicProfile());

            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            ps.setInt(10, userId);

            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

}
