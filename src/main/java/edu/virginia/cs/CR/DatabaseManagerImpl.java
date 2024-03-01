package edu.virginia.cs.CR;

import java.sql.*;
import java.util.*;


public class DatabaseManagerImpl {
    private Connection connection;
    String url = "jdbc:sqlite:Reviews.sqlite3";


    public Connection connect() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void createTables() {
        String studentTable = "CREATE TABLE IF NOT EXISTS Student (\n"
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " Username VARCHAR(50) NOT NULL,\n"
                + " Password VARCHAR(50) NOT NULL\n"
                + ");";

        String coursesTable = "CREATE TABLE IF NOT EXISTS Course (\n"
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " Department VARCHAR(10) NOT NULL,\n"
                + " CatalogNum VARCHAR(10) NOT NULL\n"
                + ");";

        String reviewTable = "CREATE TABLE IF NOT EXISTS Review (\n"
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " StudentID INTEGER NOT NULL,\n"
                + " CourseID INTEGER NOT NULL,\n"
                + " Message TEXT NOT NULL,\n"
                + " Rating INTEGER NOT NULL CHECK (Rating >= 1 AND Rating <= 5),\n"
                + " FOREIGN KEY (StudentID) REFERENCES Student(ID) ON DELETE CASCADE,\n"
                + " FOREIGN KEY (CourseID) REFERENCES Course(ID) ON DELETE CASCADE\n"
                + ");";

        try {
            Statement statement = connection.createStatement();
            if(!doTablesExist()){
                statement.execute(studentTable);
                statement.execute(coursesTable);
                statement.execute(reviewTable);
            }
            statement.close();
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean doTablesExist() throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name IN (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "Student");
            statement.setString(2, "Course");
            statement.setString(3, "Review");
            try (ResultSet tables = statement.executeQuery()) {
                int count = 0;
                while (tables.next()) {
                    count++;
                }
                return count == 3;
            }
        }
    }

    boolean doesManager(){
        try {
            return connection == null || connection.isClosed();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try {
            if(doesManager()) {
                throw new IllegalStateException("ERROR: Manager not connected yet");
            }
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM Student;");
            statement.execute("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Student';"); // reset auto-increment
            statement.execute("DELETE FROM Course;");
            statement.execute("DELETE FROM Review;");
            statement.execute("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Course';");
            statement.execute("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Review';");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteTables() {
        try {
            if(doesManager()){throw new IllegalStateException("ERROR: Manager not connected yet");}
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS Student;");
            statement.execute("DROP TABLE IF EXISTS Course;");
            statement.execute("DROP TABLE IF EXISTS Review;");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean studentExists(Student student) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM Student " +
                            "WHERE Username = ? AND Password = ?");
            statement.setString(1, student.getStudentUserName());
            statement.setString(2, student.getStudentPassword());
            ResultSet result = statement.executeQuery();
            boolean studentExists = result.next() && result.getInt("count") > 0;
            result.close();
            statement.close();
            return studentExists;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void addStudent(Student student) {
        try {
            if(doesManager()){
                throw new IllegalStateException("ERROR: Manager not connected yet");
            }
            if (!studentExists(student)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Student (Username, Password) VALUES (?, ?);");
                statement.setString(1, student.getStudentUserName());
                statement.setString(2, student.getStudentPassword());
                statement.executeUpdate();
                statement.close();
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM Student";
        List<Student> studentsList = new ArrayList<>();
        if(doesManager()){
            throw new IllegalStateException("ERROR: Manager not connected yet");
        }
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("Username"),
                        rs.getString("Password")
                        );
                studentsList.add(student);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return studentsList;
    }

    public void addCourse(Course course) {
        try {
            if(doesManager()){
                throw new IllegalStateException("ERROR: Manager not connected yet");
            }
            if (!courseExists(course)){
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Course (Department, CatalogNum) VALUES (?, ?);");
                statement.setString(1, course.getCourseDepartment());
                statement.setString(2, course.getCourseCatalogNumber());
                statement.executeUpdate();
                statement.close();
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean courseExists(Course course) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM Course " +
                            "WHERE Department = ? AND CatalogNum = ?");
            statement.setString(1, course.getCourseDepartment());
            statement.setString(2, course.getCourseCatalogNumber());
            ResultSet result = statement.executeQuery();
            boolean courseExists = result.next() && result.getInt("count") > 0;
            result.close();
            statement.close();
            return courseExists;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean reviewExistsForCourse(Student student, Course course) throws SQLException {
        int studentID = student.getStudentID(connection);
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM Review r " +
                            "JOIN Course c ON r.CourseID = c.ID " +
                            "WHERE r.StudentID = ? AND c.Department = ? AND c.CatalogNum = ?");
            statement.setInt(1, studentID);
            statement.setString(2, course.getCourseDepartment());
            statement.setString(3, course.getCourseCatalogNumber());
            ResultSet result = statement.executeQuery();
            boolean reviewExists = result.next() && result.getInt("count") > 0;
            result.close();
            statement.close();
            return reviewExists;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Handle exception
            }
        }
    }
    public List<Review> getReviewsByCourseId(int courseId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        DatabaseManagerImpl db = new DatabaseManagerImpl();
        Connection connection = db.connect();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT r.*, s.Username FROM Review r " +
                        "JOIN Student s ON r.StudentID = s.ID " +
                        "WHERE r.CourseID = ?")) {
            statement.setInt(1, courseId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("StudentID"),
                        rs.getInt("CourseID"),
                        rs.getString("Message"),
                        rs.getInt("Rating"));
                reviews.add(review);
            }

        }
        finally {
            db.close();
        }
        return reviews;
    }


}