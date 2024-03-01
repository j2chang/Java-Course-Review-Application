package edu.virginia.cs.gui;

import edu.virginia.cs.CR.Course;
import edu.virginia.cs.CR.DatabaseManagerImpl;
import edu.virginia.cs.CR.Review;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static edu.virginia.cs.gui.reviewGUICon.currentCourse;

public class reviewDisplayCon {
    @FXML
    private Label courseLabel;
    @FXML
    private Button but;
    @FXML
    private TextArea reviewsTextArea;
    @FXML
    private Label averageLabel;
    @FXML
    private TextField departmentFieldView;
    @FXML
    private TextField catalogNumFieldView;

    @FXML
    protected void whichCourseSubmit(ActionEvent event) throws IOException {
        String courseDept = departmentFieldView.getText().toUpperCase();
        String catalogNum = catalogNumFieldView.getText();

        if (courseDept.length() <= 4 && catalogNum.length() == 4) {
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
                        FXMLLoader newLoader = new FXMLLoader(getClass().getResource("displayReviews.fxml"));
                        Parent newRoot = newLoader.load();
                        Scene loginScene = new Scene(newRoot, 900, 800);
                        Stage stage = new Stage();
                        stage.setScene(loginScene);
                        stage.show();

                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();

                    } else {
                        FXMLLoader newLoader = new FXMLLoader(getClass().getResource("invalidCourseReview.fxml"));
                        Parent newRoot = newLoader.load();
                        Scene loginScene = new Scene(newRoot, 900, 800);
                        Stage stage = new Stage();
                        stage.setScene(loginScene);
                        stage.show();

                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
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
    protected void meow(ActionEvent event) throws SQLException {
        String courseDept = currentCourse.getCourseDepartment().toUpperCase();
        String catalogNum = currentCourse.getCourseCatalogNumber();

        DatabaseManagerImpl db = new DatabaseManagerImpl();
        Connection connection = db.connect();

        Course course = new Course(courseDept, catalogNum);
        currentCourse = course;
        List<Review> reviews = db.getReviewsByCourseId(currentCourse.getCourseIO(connection));

        // Update UI elements
        courseLabel.setText("Course Name: " + courseDept + " " + catalogNum);
        double averageRating = 0;
        if (!reviews.isEmpty()) {
            for (Review review : reviews) {
                reviewsTextArea.appendText("Review: " + review.getMessage() + "\n");
                averageRating += review.getRating();
            }
            averageRating /= reviews.size();
        }
        averageLabel.setText("Course Average: " + String.format("%.1f/5", averageRating));
        but.setDisable(true);
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
}
