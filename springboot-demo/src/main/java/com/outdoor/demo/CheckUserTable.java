package com.outdoor.demo;

import java.sql.*;

public class CheckUserTable {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/outdoor_demo?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "Azyl200508";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // 获取user表的所有列
            ResultSet columns = metaData.getColumns(null, null, "user", null);
            
            System.out.println("User table columns:");
            System.out.println("Column Name | Type Name | Column Size");
            System.out.println("----------------------------------");
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                
                System.out.printf("%s | %s | %d\n", columnName, typeName, columnSize);
            }
            
            // 尝试查询所有用户记录
            System.out.println("\nUser records:");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");
            
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            // 打印列名
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMetaData.getColumnName(i) + "\t");
            }
            System.out.println();
            
            // 打印数据
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}