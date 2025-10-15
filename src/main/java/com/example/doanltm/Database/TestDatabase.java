package com.example.doanltm.Database;

import com.example.doanltm.Model.DangKy;

import java.sql.*;
import java.time.LocalDate;

public class TestDatabase {
    public static void main(String[] args) {
        testConnection();
        testTableStructure();
        testInsert();
    }
    
    private static void testConnection() {
        System.out.println("=== TEST DATABASE CONNECTION ===");
        Connection conn = BDConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
        }
    }
    
    private static void testTableStructure() {
        System.out.println("\n=== TEST TABLE STRUCTURE ===");
        try (Connection conn = BDConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "dangkycalam", null);
            
            System.out.println("Table 'dangkycalam' columns:");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                String isNullable = rs.getString("IS_NULLABLE");
                String columnDefault = rs.getString("COLUMN_DEF");
                
                System.out.println("- " + columnName + " (" + columnType + 
                    ", size=" + columnSize + 
                    ", nullable=" + isNullable + 
                    ", default=" + columnDefault + ")");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting table structure: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testInsert() {
        System.out.println("\n=== TEST INSERT ===");
        
        // Test ca bình thường
        DangKy dangKy = new DangKy(2, 2, LocalDate.now());
        System.out.println("Testing insert with: " + dangKy);
        
        DangKyDAO dao = new DangKyDAO();
        boolean result = dao.dangKyCaLam(dangKy);
        
        if (result) {
            System.out.println("✅ Insert successful! Generated ID: " + dangKy.getMaDangky());
        } else {
            System.out.println("❌ Insert failed!");
        }
    }
}