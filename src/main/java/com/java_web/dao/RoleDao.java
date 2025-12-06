package com.java_web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.java_web.config.DB;
import com.java_web.model.auth.Role;

public class RoleDao {

    private static final String SQL = "select RoleName, Description from auth.Roles order by RoleName";

    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        try (Connection conn = DB.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role r = new Role();
                r.setRoleName(rs.getString("roleName"));
                r.setDescription(rs.getString("description"));
                roles.add(r);
            }
        }
        return roles;
    }
}
