
package com.university;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentController {

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> idColumn;

    @FXML
    private TableColumn<Student, String> nameColumn;

    @FXML
    private TableColumn<Student, String> emailColumn;

    @FXML
    private TableColumn<Student, String> programColumn;

    @FXML
    private TableColumn<Student, String> statusColumn;

    private final ObservableList<Student> students =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        programColumn.setCellValueFactory(
                new PropertyValueFactory<>("programName"));

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        studentTable.setItems(students);

        loadStudents();
    }

    public void loadStudents() {

        LoadStudentsTask task = new LoadStudentsTask();

        task.setOnSucceeded(event ->
                students.setAll(task.getValue()));

        task.setOnFailed(event ->
                System.out.println(
                        "Student loading failed: " +
                        task.getException().getMessage()));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public ObservableList<Student> getStudents() {
        return students;
    }
}
