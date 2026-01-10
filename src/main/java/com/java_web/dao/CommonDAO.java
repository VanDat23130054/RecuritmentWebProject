package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.common.City;
import com.java_web.model.common.EmploymentType;
import com.java_web.model.common.RemoteType;
import com.java_web.model.common.SeniorityLevel;
import com.java_web.model.common.Skill;

public class CommonDAO {

    public List<City> getAllCities() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "{call common.sp_GetAllCities}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                City city = new City();
                city.setCityId(rs.getInt("CityID"));
                city.setName(rs.getString("CityName"));
                city.setProvinceId(rs.getInt("ProvinceID"));
                cities.add(city);
            }
        }
        return cities;
    }

    public List<Skill> getTopSkills(int limit) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        String sql = "{call common.sp_GetTopSkills(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill();
                    skill.setSkillId(rs.getInt("SkillID"));
                    skill.setName(rs.getString("SkillName"));
                    skill.setSlug(rs.getString("Slug"));
                    skills.add(skill);
                }
            }
        }
        return skills;
    }

    public List<EmploymentType> getEmploymentTypes() throws SQLException {
        List<EmploymentType> types = new ArrayList<>();
        String sql = "{call common.sp_GetEmploymentTypes}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EmploymentType type = new EmploymentType();
                type.setEmploymentTypeId(rs.getShort("EmploymentTypeID"));
                type.setName(rs.getString("Name"));
                types.add(type);
            }
        }
        return types;
    }

    public List<SeniorityLevel> getSeniorityLevels() throws SQLException {
        List<SeniorityLevel> levels = new ArrayList<>();
        String sql = "{call common.sp_GetSeniorityLevels}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SeniorityLevel level = new SeniorityLevel();
                level.setSeniorityLevelId(rs.getShort("SeniorityLevelID"));
                level.setName(rs.getString("Name"));
                levels.add(level);
            }
        }
        return levels;
    }

    public List<RemoteType> getRemoteTypes() throws SQLException {
        List<RemoteType> types = new ArrayList<>();
        String sql = "{call common.sp_GetRemoteTypes}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RemoteType type = new RemoteType();
                type.setRemoteTypeId(rs.getShort("RemoteTypeID"));
                type.setName(rs.getString("Name"));
                types.add(type);
            }
        }
        return types;
    }

    /**
     * Get total count of published jobs
     */
    public int getTotalJobCount() throws SQLException {
        String sql = "{call common.sp_GetTotalJobCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }

    /**
     * Get total count of companies
     */
    public int getTotalCompanyCount() throws SQLException {
        String sql = "{call common.sp_GetTotalCompanyCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }

    /**
     * Get total count of candidates
     */
    public int getTotalCandidateCount() throws SQLException {
        String sql = "{call common.sp_GetTotalCandidateCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }

    /**
     * Get total count of applications
     */
    public int getTotalApplicationCount() throws SQLException {
        String sql = "{call common.sp_GetTotalApplicationCount(?)}";

        try (Connection conn = DB.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(1);
        }
    }
}
