
package com.university;

import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoadStudentsTask extends Task<ObservableList<Student>> {

    @Override
    protected ObservableList<Student> call() throws Exception {

        ObservableList<Student> students =
                FXCollections.observableArrayList();

        String sql =
                "SELECT s.student_id, s.full_name, s.email, " +
                "p.program_name, s.status " +
                "FROM students s " +
                "LEFT JOIN programs p " +
                "ON s.program_id = p.program_id " +
                "ORDER BY s.created_at DESC";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                students.add(new Student(
                        result.getString("student_id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        result.getString("program_name"),
                        result.getString("status")
                ));
            }
        }

        return students;
    }
}
