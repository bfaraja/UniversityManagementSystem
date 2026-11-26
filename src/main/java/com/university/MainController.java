
package com.university;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane root;

    @FXML
    private void showStudents() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("StudentView.fxml")
        );

        VBox studentView = loader.load();
        root.setCenter(studentView);
    }
}
