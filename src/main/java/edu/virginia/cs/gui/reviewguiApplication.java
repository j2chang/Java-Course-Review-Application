package edu.virginia.cs.gui;

import edu.virginia.cs.CR.DatabaseManagerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class reviewguiApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(reviewguiApplication.class.getResource("review-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Review");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseManagerImpl db = new DatabaseManagerImpl();
        db.connect();
        db.createTables();
        launch(args);
    }
}
