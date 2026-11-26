
package com.university;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {

    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty programName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Student(String studentId, String fullName, String email,
                   String programName, String status) {
        this.studentId.set(studentId);
        this.fullName.set(fullName);
        this.email.set(email);
        this.programName.set(programName);
        this.status.set(status);
    }

    public String getStudentId() { return studentId.get(); }
    public StringProperty studentIdProperty() { return studentId; }

    public String getFullName() { return fullName.get(); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public String getProgramName() { return programName.get(); }
    public StringProperty programNameProperty() { return programName; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
