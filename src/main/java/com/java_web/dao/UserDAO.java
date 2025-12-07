package com.java_web.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.java_web.config.DB;
import com.java_web.model.auth.User;

public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        String sql = "{call auth.sp_GetUserByEmail(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setEmail(rs.getString("Email"));
                    user.setPasswordHash(rs.getBytes("PasswordHash"));
                    user.setSalt(rs.getBytes("Salt"));
                    user.setRole(rs.getString("Role"));
                    user.setEmailConfirmed(rs.getBoolean("IsEmailConfirmed"));
                    user.setActive(rs.getBoolean("IsActive"));
                    
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    
                    Timestamp lastLoginAt = rs.getTimestamp("LastLoginAt");
                    if (lastLoginAt != null) {
                        user.setLastLoginAt(lastLoginAt.toLocalDateTime());
                    }
                    
                    return user;
                }
            }
        }
        return null;
    }

    public User findById(Integer userId) throws SQLException {
        String sql = "{call auth.sp_GetUserById(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setEmail(rs.getString("Email"));
                    user.setPasswordHash(rs.getBytes("PasswordHash"));
                    user.setSalt(rs.getBytes("Salt"));
                    user.setRole(rs.getString("Role"));
                    user.setEmailConfirmed(rs.getBoolean("IsEmailConfirmed"));
                    user.setActive(rs.getBoolean("IsActive"));
                    
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    if (createdAt != null) {
                        user.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    
                    Timestamp lastLoginAt = rs.getTimestamp("LastLoginAt");
                    if (lastLoginAt != null) {
                        user.setLastLoginAt(lastLoginAt.toLocalDateTime());
                    }
                    
                    return user;
                }
            }
        }
        return null;
    }

    public Integer createUser(User user) throws SQLException {
        String sql = "{call auth.sp_CreateUser(?, ?, ?, ?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setBytes(2, user.getPasswordHash());
            stmt.setBytes(3, user.getSalt());
            stmt.setString(4, user.getRole());
            stmt.registerOutParameter(5, Types.INTEGER);
            
            stmt.execute();
            return stmt.getInt(5);
        }
    }

    public void updateLastLogin(Integer userId) throws SQLException {
        String sql = "{call auth.sp_UpdateLastLogin(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.execute();
        }
    }

    public void confirmEmail(Integer userId) throws SQLException {
        String sql = "{call auth.sp_ConfirmEmail(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.execute();
        }
    }

    public void updatePassword(Integer userId, byte[] passwordHash, byte[] salt) throws SQLException {
        String sql = "{call auth.sp_UpdatePassword(?, ?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setBytes(2, passwordHash);
            stmt.setBytes(3, salt);
            stmt.execute();
        }
    }

    public void deactivateUser(Integer userId) throws SQLException {
        String sql = "{call auth.sp_DeactivateUser(?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.execute();
        }
    }
}
