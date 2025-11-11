package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
