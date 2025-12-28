import java.sql.*;

public class CheckAndUpdateDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/outdoor_demo?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
        String username = "root";
        String password = "Azyl200508";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("成功连接到数据库");
            
            // 检查personal_equipment表结构
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "personal_equipment", "note");
            
            if (rs.next()) {
                System.out.println("note列已经存在");
            } else {
                System.out.println("note列不存在，正在添加...");
                // 手动添加note列
                String sql = "ALTER TABLE personal_equipment ADD COLUMN note VARCHAR(255) NULL";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    System.out.println("成功添加note列");
                }
            }
            
            // 再次检查表结构以确认
            System.out.println("\n个人装备表结构：");
            String describeSql = "DESCRIBE personal_equipment";
            try (Statement stmt = conn.createStatement();
                 ResultSet describeRs = stmt.executeQuery(describeSql)) {
                while (describeRs.next()) {
                    String field = describeRs.getString(1);
                    String type = describeRs.getString(2);
                    String nullAble = describeRs.getString(3);
                    System.out.println(field + "\t" + type + "\t" + nullAble);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}