package com.outdoor.demo;

import java.sql.*;

public class TestUserTable {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/outdoor_demo?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "Azyl200508";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            Statement stmt = conn.createStatement();
            
            // 测试查询user表的nickname字段
            ResultSet rs = stmt.executeQuery("SELECT id, username, nickname, avatar_url, gender, birthday, bio, phone FROM user");
            
            System.out.println("User table columns test passed!");
            System.out.println("Query executed successfully: SELECT id, username, nickname, avatar_url, gender, birthday, bio, phone FROM user");
            
            // 打印结果集信息
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("\nColumns in result set:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%d: %s (%s)\n", i, metaData.getColumnName(i), metaData.getColumnTypeName(i));
            }
            
        } catch (SQLException e) {
            System.err.println("Error testing user table columns:");
            e.printStackTrace();
        }
    }
}