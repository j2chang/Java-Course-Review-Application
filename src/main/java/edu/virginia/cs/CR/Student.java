package edu.virginia.cs.CR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student {
    private String username;
    private String password;


    public Student(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getStudentUserName(){
        return username;
    }
    public void setStudentName(String name){
        this.username = name;
    }

    public String getStudentPassword(){
        return password;
    }

    public int getStudentID(Connection connection) throws SQLException {
        String sql = "SELECT ID FROM Student WHERE Username = ? AND Password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                } else {
                    return -1; // student not found
                }
            }
        }
    }

    public void setStudentPassword(String password){
        this.password = password;
    }
}
