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
import com.java_web.model.common.City;
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

    public List<Map<String, String>> getEmploymentTypes() throws SQLException {
        List<Map<String, String>> types = new ArrayList<>();
        String sql = "{call common.sp_GetEmploymentTypes}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> type = new HashMap<>();
                type.put("id", String.valueOf(rs.getInt("EmploymentTypeID")));
                type.put("name", rs.getString("Name"));
                types.add(type);
            }
        }
        return types;
    }

    public List<Map<String, String>> getSeniorityLevels() throws SQLException {
        List<Map<String, String>> levels = new ArrayList<>();
        String sql = "{call common.sp_GetSeniorityLevels}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> level = new HashMap<>();
                level.put("id", String.valueOf(rs.getInt("SeniorityLevelID")));
                level.put("name", rs.getString("Name"));
                levels.add(level);
            }
        }
        return levels;
    }

    public List<Map<String, String>> getRemoteTypes() throws SQLException {
        List<Map<String, String>> types = new ArrayList<>();
        String sql = "{call common.sp_GetRemoteTypes}";

        try (Connection conn = DB.getConnection(); 
             CallableStatement stmt = conn.prepareCall(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> type = new HashMap<>();
                type.put("id", String.valueOf(rs.getInt("RemoteTypeID")));
                type.put("name", rs.getString("Name"));
                types.add(type);
            }
        }
        return types;
    }
}
