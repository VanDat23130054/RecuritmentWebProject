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
                    user.setUserId(rs.getInt("UserId"));
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
                    user.setUserId(rs.getInt("UserId"));
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

    /**
     * Complete user registration with profile creation
     * Returns: [userId, profileId, companyId (null for candidates)]
     */
    public Object[] registerUser(String email, byte[] passwordHash, byte[] salt, 
                                 String role, String fullName, String companyName, 
                                 String recruiterTitle) throws SQLException {
        String sql = "{call auth.sp_RegisterUser(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = DB.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, email);
            stmt.setBytes(2, passwordHash);
            stmt.setBytes(3, salt);
            stmt.setString(4, role);
            stmt.setString(5, fullName);
            
            if ("Recruiter".equals(role)) {
                stmt.setString(6, companyName);
                stmt.setString(7, recruiterTitle);
            } else {
                stmt.setNull(6, Types.NVARCHAR);
                stmt.setNull(7, Types.NVARCHAR);
            }
            
            stmt.registerOutParameter(8, Types.INTEGER); // UserId
            stmt.registerOutParameter(9, Types.INTEGER); // ProfileId
            stmt.registerOutParameter(10, Types.INTEGER); // CompanyId
            
            stmt.execute();
            
            Integer userId = stmt.getInt(8);
            Integer profileId = stmt.getInt(9);
            Integer companyId = stmt.getInt(10);
            if (stmt.wasNull()) companyId = null;
            
            return new Object[] { userId, profileId, companyId };
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
