package edu.virginia.cs.gui;

import edu.virginia.cs.CR.Course;
import edu.virginia.cs.CR.DatabaseManagerImpl;
import edu.virginia.cs.CR.Review;
import edu.virginia.cs.CR.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class reviewGUICon {
    private static Student currentUser;

    public static Course currentCourse;
    @FXML
    private ComboBox<String> ratingComboBox;
    @FXML
    private TextArea reviewTextArea;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField departmentFieldFirst;
    @FXML
    private TextField catalogNumFieldFirst;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Label tryAgain;

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) throws IOException, SQLException {
        tryAgain.setVisible(false);
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Establish a connection to the database
        DatabaseManagerImpl db = new DatabaseManagerImpl();
        Connection connection = db.connect();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) AS count FROM Student " +
                            "WHERE Username = ? AND Password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            if (rs.getInt("count") > 0) {
                // Create a new Student object and set its values
                Student student = new Student(username, password);

                // Set the global student object
                currentUser = student;

                // Go to main menu
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
                Parent root = fxmlLoader.load();
                Scene loginScene = new Scene(root, 800, 600);
                Stage stage = new Stage();
                stage.setScene(loginScene);
                stage.show();

                // Close current stage
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
            } else {
                tryAgain.setVisible(true);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            db.close();
        }
    }
    @FXML
    protected void handleCreateAccountButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("create-account.fxml"));
        Parent createAccount = loader.load();
        Scene createAccountScene = new Scene(createAccount);
        Stage stage = new Stage();
        stage.setScene(createAccountScene);
        stage.show();

        // Close current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    protected void createAccountSubmit(ActionEvent event) throws IOException,  SQLException {
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();
        String confirmPasswordField = confirmPassword.getText();
        DatabaseManagerImpl db = new DatabaseManagerImpl();
        Student newStudent = new Student(username, password);
        db.connect();

        if (!password.equals(confirmPasswordField) || db.studentExists(newStudent)) {
            FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("review-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
        else {
            if ((username.length() == 0 || password.length() == 0) == false){
                // Insert new user into database
                currentUser = newStudent;

                // Connect to the database and add the new student
                db.addStudent(currentUser);
                System.out.println(currentUser);

                // Go to main menu
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
                Parent root = fxmlLoader.load();
                Scene loginScene = new Scene(root, 800, 600);
                Stage stage = new Stage();
                stage.setScene(loginScene);
                stage.show();

                // Close current stage
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();
            }
        }
        db.close();
    }
    @FXML
    protected void LogoutFromMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("review-view.fxml"));
        Parent login = loader.load();
        Scene menu = new Scene(login);
        Stage stage = new Stage();
        stage.setScene(menu);
        stage.show();

        // Close current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    protected void writingReview(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("courseSelectionPage.fxml"));
        Parent root = fxmlLoader.load();
        Scene writingReview = new Scene(root, 300, 300);
        Stage stage = new Stage();
        stage.setScene(writingReview);
        stage.show();

        // Close current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    protected void submitCourseSelection(ActionEvent event) throws IOException{
        String courseDept = departmentFieldFirst.getText().toUpperCase();
        String catalogNum = catalogNumFieldFirst.getText();

        if (courseDept.length() <= 4 && catalogNum.length() == 4){
            // Establish a connection to the database
            DatabaseManagerImpl db = new DatabaseManagerImpl();
            Connection connection = db.connect();

            try {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) AS count FROM Course " +
                                "WHERE Department = ? AND CatalogNum = ?");
                statement.setString(1, courseDept);
                statement.setString(2, catalogNum);
                ResultSet rs = statement.executeQuery();

                if (rs.next() && rs.getInt("count") > 0) {
                    Course course = new Course(courseDept, catalogNum);
                    currentCourse = course;
                    PreparedStatement statement2 = connection.prepareStatement(
                            "SELECT COUNT(*) AS count FROM Review " +
                                    "WHERE StudentID = ? AND CourseID = ?");
                    System.out.println(currentUser.getStudentID(connection));
                    System.out.println(currentCourse.getCourseIO(connection));
                    statement2.setInt(1, currentUser.getStudentID(connection));
                    statement2.setInt(2, currentCourse.getCourseIO(connection));
                    ResultSet rs2 = statement2.executeQuery();
                    if (rs2.getInt("count") > 0) {
                        FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("invalidAlreadyExist.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene loginScene = new Scene(root, 1000, 600);
                        Stage stage = new Stage();
                        stage.setScene(loginScene);
                        stage.show();

                        // Close current stage
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    }else{
                        FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("writing-review.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene loginScene = new Scene(root, 800, 600);
                        Stage stage = new Stage();
                        stage.setScene(loginScene);
                        stage.show();

                        // Close current stage
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    }
                    rs2.close();
                }
                else {
                    // Create a new Course object and add it to the database
                    Course course = new Course(courseDept, catalogNum);
                    currentCourse = course;
                    db.addCourse(course);
                    FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("writing-review.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene loginScene = new Scene(root, 800, 600);
                    Stage stage = new Stage();
                    stage.setScene(loginScene);
                    stage.show();

                    // Close current stage
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close();
                }
                rs.close();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            finally {
                db.close();
            }
        }
        else{
            FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("invalid.fxml"));
            Parent root = fxmlLoader.load();
            Scene loginScene = new Scene(root, 1000, 600);
            Stage stage = new Stage();
            stage.setScene(loginScene);
            stage.show();

            // Close current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
    }
    @FXML
    protected void submitReview(ActionEvent event) throws IOException, SQLException {
        String review = reviewTextArea.getText();
        int rating = Integer.parseInt(ratingComboBox.getValue());

        DatabaseManagerImpl db = new DatabaseManagerImpl();
        Connection connection = db.connect();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Review (StudentID, CourseID, Message, Rating) VALUES (?, ?, ?, ?);");
            statement.setInt(1, currentUser.getStudentID(connection));
            statement.setInt(2, currentCourse.getCourseIO(connection));
            statement.setString(3, review);
            statement.setInt(4, rating);
            statement.executeUpdate();
            statement.close();

            FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("main-menu.fxml"));
            Parent root = fxmlLoader.load();
            Scene loginScene = new Scene(root, 800, 600);
            Stage stage = new Stage();
            stage.setScene(loginScene);
            stage.show();
            // Close current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } finally {
            db.close();
        }
    }
    @FXML
    protected void mainMenu(ActionEvent event) throws IOException {
        // Go to main menu
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
        Parent root = fxmlLoader.load();
        Scene loginScene = new Scene(root, 800, 600);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.show();

        // Close current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    protected void selectWhichCourse(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("selectWhichCoursePage.fxml"));
        Parent root = fxmlLoader.load();
        Scene writingReview = new Scene(root, 300, 300);
        Stage stage = new Stage();
        stage.setScene(writingReview);
        stage.show();

        // Close current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
    @FXML
    protected void test(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("displayReviews.fxml"));
        Parent root = fxmlLoader.load();
        Scene loginScene = new Scene(root, 800, 600);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.show();
    }



}