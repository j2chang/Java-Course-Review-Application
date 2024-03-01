module CourseReview.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens edu.virginia.cs.gui to javafx.fxml;
    exports edu.virginia.cs.gui;

}