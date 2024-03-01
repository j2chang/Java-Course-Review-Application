package edu.virginia.cs.CR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Course {
    private String department;
    private String catalogNumber;


    public Course(String department, String catalogNumber) {
        this.department = department;
        this.catalogNumber = catalogNumber;
    }

    public String getCourseDepartment(){
        return department;
    }
    public void setCourseDepartment(String department){
        this.department = department;
    }

    public String getCourseCatalogNumber(){
        return catalogNumber;
    }

    public int getCourseIO(Connection connection) throws SQLException {
        String sql = "SELECT ID FROM Course WHERE Department = ? AND CatalogNum = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, this.department);
            pstmt.setString(2, this.catalogNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                } else {
                    return -1; // course not found
                }
            }
        }
    }

    public void setCourseCatalogNumber(String catalogNumber){
        this.catalogNumber = catalogNumber;
    }
}
