package com.outdoor.demo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/outdoor_demo?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "Azyl200508";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "user", null);
            
            System.out.println("User table columns:");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                System.out.printf("%-15s %-15s %d%n", columnName, columnType, columnSize);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}