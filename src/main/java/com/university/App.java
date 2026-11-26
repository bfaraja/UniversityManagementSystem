
package com.university;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class App extends Application {

    private BorderPane root;
    private VBox sidebar;

    private ObservableList<StudentRow> students =
            FXCollections.observableArrayList();

    private ObservableList<ProgramRow> programRows =
            FXCollections.observableArrayList();

    private ObservableList<String> programNames =
            FXCollections.observableArrayList();

    private Label studentCountLabel;
    private Label programCountLabel;
    private Label systemStatusLabel;
    private Label heartbeatLabel;

    private Button dashboardButton;
    private Button studentsButton;
    private Button programsButton;
    private Button settingsButton;

    private ScheduledExecutorService scheduler;
    private final AtomicBoolean refreshInProgress =
            new AtomicBoolean(false);

    private String currentPage = "Dashboard";

    @Override
    public void start(Stage stage) {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6fb;");

        createSidebar();
        showDashboard();

        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("University Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();

        // Initial database loading
        refreshData();

        // Scheduled heartbeat and refresh every 5 seconds
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (heartbeatLabel != null) {
                    heartbeatLabel.setText(
                            "Last heartbeat: " +
                            java.time.LocalTime.now()
                                    .withNano(0)
                    );
                }
            });

            refreshData();

        }, 5, 5, TimeUnit.SECONDS);

        System.out.println("University Management System started.");
    }

    // SIDEBAR
    private void createSidebar() {

        sidebar = new VBox(20);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: #172554;");

        Label university = new Label("UNIVERSITY");
        university.setFont(Font.font(22));
        university.setTextFill(Color.WHITE);
        university.setStyle("-fx-font-weight: bold;");

        Label subtitle = new Label("MANAGEMENT SYSTEM");
        subtitle.setTextFill(Color.LIGHTBLUE);

        dashboardButton = createSidebarButton("Dashboard");
        studentsButton = createSidebarButton("Students");
        programsButton = createSidebarButton("Programs");
        settingsButton = createSidebarButton("Settings");

        dashboardButton.setOnAction(e -> showDashboard());
        studentsButton.setOnAction(e -> showStudents());
        programsButton.setOnAction(e -> showPrograms());
        settingsButton.setOnAction(e -> showSettings());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label version = new Label("University System v1.0");
        version.setTextFill(Color.LIGHTGRAY);

        sidebar.getChildren().addAll(
                university,
                subtitle,
                new Separator(),
                dashboardButton,
                studentsButton,
                programsButton,
                settingsButton,
                spacer,
                version
        );

        root.setLeft(sidebar);
    }

    private Button createSidebarButton(String text) {

        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12));

        return button;
    }

    private void updateSidebarSelection(String selected) {

        currentPage = selected;

        Button[] buttons = {
                dashboardButton,
                studentsButton,
                programsButton,
                settingsButton
        };

        String[] names = {
                "Dashboard",
                "Students",
                "Programs",
                "Settings"
        };

        for (int i = 0; i < buttons.length; i++) {

            if (names[i].equals(selected)) {
                buttons[i].setStyle(
                        "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
                );
            } else {
                buttons[i].setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;"
                );
            }
        }
    }

    // PAGE HEADER
    private HBox createHeader(
            String titleText,
            String subtitleText
    ) {

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox heading = new VBox(5);

        Label title = new Label(titleText);
        title.setFont(Font.font(28));
        title.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172554;"
        );

        Label subtitle = new Label(subtitleText);
        subtitle.setStyle("-fx-text-fill: #64748b;");

        heading.getChildren().addAll(title, subtitle);
        HBox.setHgrow(heading, Priority.ALWAYS);

        Label admin = new Label("ADMIN");
        admin.setPadding(new Insets(10, 15, 10, 15));
        admin.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-text-fill: #1d4ed8;" +
                "-fx-background-radius: 20;" +
                "-fx-font-weight: bold;"
        );

        header.getChildren().addAll(heading, admin);

        return header;
    }

    // DASHBOARD
    private void showDashboard() {

        updateSidebarSelection("Dashboard");

        VBox content = new VBox(22);
        content.setPadding(new Insets(30));

        HBox header = createHeader(
                "Dashboard",
                "Welcome to the University Management System"
        );

        HBox cards = new HBox(15);

        VBox studentsCard = createCard(
                "Total Students",
                String.valueOf(students.size()),
                "Registered students"
        );

        VBox programsCard = createCard(
                "Total Programs",
                String.valueOf(programRows.size()),
                "Academic programs"
        );

        VBox statusCard = createCard(
                "System Status",
                "Online",
                "System operational"
        );

        studentCountLabel =
                (Label) studentsCard.getChildren().get(1);

        programCountLabel =
                (Label) programsCard.getChildren().get(1);

        systemStatusLabel =
                (Label) statusCard.getChildren().get(1);

        cards.getChildren().addAll(
                studentsCard,
                programsCard,
                statusCard
        );

        VBox tableSection = new VBox(15);
        tableSection.setPadding(new Insets(20));
        tableSection.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        HBox tableHeader = new HBox(10);
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        VBox tableHeading = new VBox(5);

        Label recentStudents = new Label("Recent Students");
        recentStudents.setFont(Font.font(20));
        recentStudents.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172554;"
        );

        Label description = new Label(
                "Student records retrieved from MySQL"
        );
        description.setStyle("-fx-text-fill: #64748b;");

        tableHeading.getChildren().addAll(
                recentStudents, description
        );

        HBox.setHgrow(tableHeading, Priority.ALWAYS);

        Button addButton = createAddButton();
        tableHeader.getChildren().addAll(
                tableHeading, addButton
        );

        TableView<StudentRow> table = createStudentTable();

        VBox.setVgrow(table, Priority.ALWAYS);

        tableSection.getChildren().addAll(
                tableHeader, table
        );

        content.getChildren().addAll(
                header, cards, tableSection
        );

        root.setCenter(content);
    }

    // STUDENTS PAGE
    private void showStudents() {

        updateSidebarSelection("Students");

        VBox content = new VBox(22);
        content.setPadding(new Insets(30));

        HBox header = createHeader(
                "Students",
                "Manage student records"
        );

        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox sectionHeader = new HBox(10);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("All Students");
        heading.setFont(Font.font(20));
        heading.setStyle("-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = createAddButton();

        sectionHeader.getChildren().addAll(
                heading, spacer, addButton
        );

        TableView<StudentRow> table = createStudentTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        section.getChildren().addAll(sectionHeader, table);

        content.getChildren().addAll(header, section);

        root.setCenter(content);
    }

    // PROGRAMS PAGE
    private void showPrograms() {

        updateSidebarSelection("Programs");

        VBox content = new VBox(22);
        content.setPadding(new Insets(30));

        HBox header = createHeader(
                "Programs",
                "Academic programs registered in the system"
        );

        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );
        VBox.setVgrow(section, Priority.ALWAYS);

        Label heading = new Label("Academic Programs");
        heading.setFont(Font.font(20));
        heading.setStyle("-fx-font-weight: bold;");

        TableView<ProgramRow> table = new TableView<>();
        table.setItems(programRows);
        table.setPlaceholder(
                new Label("No programs found in database.")
        );

        TableColumn<ProgramRow, String> idColumn =
                new TableColumn<>("Program ID");

        idColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "id"
                )
        );

        TableColumn<ProgramRow, String> nameColumn =
                new TableColumn<>("Program Name");

        nameColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "name"
                )
        );

        TableColumn<ProgramRow, String> descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "description"
                )
        );

        table.getColumns().addAll(
                idColumn,
                nameColumn,
                descriptionColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        section.getChildren().addAll(heading, table);

        content.getChildren().addAll(header, section);

        root.setCenter(content);
    }

    // SETTINGS PAGE
    private void showSettings() {

        updateSidebarSelection("Settings");

        VBox content = new VBox(22);
        content.setPadding(new Insets(30));

        HBox header = createHeader(
                "Settings",
                "System information and connection status"
        );

        VBox section = new VBox(18);
        section.setPadding(new Insets(25));
        section.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );

        Label heading = new Label("System Information");
        heading.setFont(Font.font(20));
        heading.setStyle("-fx-font-weight: bold;");

        Label appName = new Label(
                "Application: University Management System"
        );

        Label architecture = new Label(
                "Architecture: JavaFX Event-Driven Application"
        );

        Label database = new Label(
                "Database: MySQL - university_db"
        );

        Label connection = new Label(
                "Connection: localhost:3306"
        );

        heartbeatLabel = new Label(
                "Heartbeat: Scheduled every 5 seconds"
        );

        Label status = new Label(
                "System status: Running"
        );

        status.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        section.getChildren().addAll(
                heading,
                appName,
                architecture,
                database,
                connection,
                heartbeatLabel,
                status
        );

        content.getChildren().addAll(header, section);

        root.setCenter(content);
    }

    // STUDENT TABLE
    private TableView<StudentRow> createStudentTable() {

        TableView<StudentRow> table = new TableView<>();
        table.setItems(students);

        table.setPlaceholder(
                new Label("No student records available.")
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        TableColumn<StudentRow, String> idColumn =
                new TableColumn<>("Student ID");

        idColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "id"
                )
        );

        TableColumn<StudentRow, String> nameColumn =
                new TableColumn<>("Full Name");

        nameColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "name"
                )
        );

        TableColumn<StudentRow, String> emailColumn =
                new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "email"
                )
        );

        TableColumn<StudentRow, String> programColumn =
                new TableColumn<>("Program");

        programColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "program"
                )
        );

        TableColumn<StudentRow, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(
                        "status"
                )
        );

        table.getColumns().addAll(
                idColumn,
                nameColumn,
                emailColumn,
                programColumn,
                statusColumn
        );

        return table;
    }

    // ADD STUDENT BUTTON
    private Button createAddButton() {

        Button button = new Button("+ Add Student");

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10 18;" +
                "-fx-background-radius: 8;" +
                "-fx-font-weight: bold;"
        );

        button.setOnAction(event -> showAddStudentDialog());

        return button;
    }

    // DASHBOARD CARD
    private VBox createCard(
            String heading,
            String value,
            String description
    ) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;"
        );

        HBox.setHgrow(card, Priority.ALWAYS);

        Label headingLabel = new Label(heading);
        headingLabel.setStyle("-fx-text-fill: #64748b;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font(30));
        valueLabel.setStyle(
                "-fx-text-fill: #172554;" +
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle("-fx-text-fill: #94a3b8;");

        card.getChildren().addAll(
                headingLabel,
                valueLabel,
                descriptionLabel
        );

        return card;
    }

    // BACKGROUND DATABASE REFRESH
    private void refreshData() {

        if (!refreshInProgress.compareAndSet(false, true)) {
            return;
        }

        Task<DashboardData> task = new Task<>() {

            @Override
            protected DashboardData call() throws Exception {

                List<StudentRow> loadedStudents =
                        new ArrayList<>();

                List<ProgramRow> loadedPrograms =
                        new ArrayList<>();

                List<String> loadedProgramNames =
                        new ArrayList<>();

                // Load students
                String studentSql =
                        "SELECT s.student_id, s.full_name, s.email, " +
                        "p.program_name, s.status " +
                        "FROM students s " +
                        "LEFT JOIN programs p " +
                        "ON s.program_id = p.program_id " +
                        "ORDER BY s.created_at DESC";

                try (Connection connection =
                             DatabaseUtil.getConnection();
                     Statement statement =
                             connection.createStatement();
                     ResultSet result =
                             statement.executeQuery(studentSql)) {

                    while (result.next()) {

                        loadedStudents.add(
                                new StudentRow(
                                        result.getString("student_id"),
                                        result.getString("full_name"),
                                        result.getString("email"),
                                        result.getString("program_name"),
                                        result.getString("status")
                                )
                        );
                    }
                }

                // Load programs
                String programSql =
                        "SELECT program_id, program_name, description " +
                        "FROM programs ORDER BY program_name";

                try (Connection connection =
                             DatabaseUtil.getConnection();
                     Statement statement =
                             connection.createStatement();
                     ResultSet result =
                             statement.executeQuery(programSql)) {

                    while (result.next()) {

                        loadedPrograms.add(
                                new ProgramRow(
                                        result.getString("program_id"),
                                        result.getString("program_name"),
                                        result.getString("description")
                                )
                        );

                        loadedProgramNames.add(
                                result.getString("program_name")
                        );
                    }
                }

                return new DashboardData(
                        loadedStudents,
                        loadedPrograms,
                        loadedProgramNames
                );
            }
        };

        task.setOnSucceeded(event -> {

            DashboardData data = task.getValue();

            students.setAll(data.students);
            programRows.setAll(data.programs);
            programNames.setAll(data.programNames);

            if (studentCountLabel != null) {
                studentCountLabel.setText(
                        String.valueOf(students.size())
                );
            }

            if (programCountLabel != null) {
                programCountLabel.setText(
                        String.valueOf(programRows.size())
                );
            }

            if (systemStatusLabel != null) {
                systemStatusLabel.setText("Online");
            }

            refreshInProgress.set(false);
        });

        task.setOnFailed(event -> {

            System.err.println(
                    "Database refresh failed: " +
                    task.getException().getMessage()
            );

            if (systemStatusLabel != null) {
                systemStatusLabel.setText("Error");
            }

            refreshInProgress.set(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // ADD STUDENT DIALOG
    private void showAddStudentDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Student");

        ButtonType saveButton = new ButtonType(
                "Save",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");

        ComboBox<String> programBox =
                new ComboBox<>(programNames);

        programBox.setPromptText("Select Program");
        programBox.setMaxWidth(Double.MAX_VALUE);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        form.add(new Label("Student ID:"), 0, 0);
        form.add(idField, 1, 0);

        form.add(new Label("Full Name:"), 0, 1);
        form.add(nameField, 1, 1);

        form.add(new Label("Email:"), 0, 2);
        form.add(emailField, 1, 2);

        form.add(new Label("Program:"), 0, 3);
        form.add(programBox, 1, 3);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {

            if (result == saveButton) {

                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String program = programBox.getValue();

                if (id.isEmpty()
                        || name.isEmpty()
                        || email.isEmpty()
                        || program == null) {

                    showWarning(
                            "Fill in all fields and select a program."
                    );
                    return;
                }

                saveStudent(id, name, email, program);
            }
        });
    }

    // SAVE STUDENT IN BACKGROUND
    private void saveStudent(
            String id,
            String name,
            String email,
            String program
    ) {

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                String sql =
                        "INSERT INTO students " +
                        "(student_id, full_name, email, program_id, status) " +
                        "VALUES (?, ?, ?, " +
                        "(SELECT program_id FROM programs " +
                        "WHERE program_name = ?), 'Active')";

                try (Connection connection =
                             DatabaseUtil.getConnection();
                     PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, id);
                    statement.setString(2, name);
                    statement.setString(3, email);
                    statement.setString(4, program);

                    statement.executeUpdate();
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {

            showInformation(
                    "Student saved successfully!",
                    name + " has been added to the database."
            );

            // EVENT: StudentAdded
            publishEvent("StudentAdded");

            refreshData();
        });

        task.setOnFailed(event -> {

            showError(
                    "Could not save student",
                    task.getException().getMessage()
            );
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // SIMPLE EVENT DISPATCH
    private void publishEvent(String eventName) {

        System.out.println(
                "EVENT DISPATCHED: " + eventName
        );

        if (eventName.equals("StudentAdded")) {
            System.out.println(
                    "Student-added event received. Refreshing student records."
            );
        }
    }

    // ALERTS
    private void showWarning(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing Information");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showInformation(
            String heading,
            String message
    ) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(heading);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(
            String heading,
            String message
    ) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(heading);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // SHUTDOWN
    @Override
    public void stop() {

        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    // STUDENT MODEL
    public static class StudentRow {

        private final String id;
        private final String name;
        private final String email;
        private final String program;
        private final String status;

        public StudentRow(
                String id,
                String name,
                String email,
                String program,
                String status
        ) {

            this.id = id;
            this.name = name;
            this.email = email;
            this.program = program;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getProgram() {
            return program == null ? "N/A" : program;
        }

        public String getStatus() {
            return status;
        }
    }

    // PROGRAM MODEL
    public static class ProgramRow {

        private final String id;
        private final String name;
        private final String description;

        public ProgramRow(
                String id,
                String name,
                String description
        ) {

            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    // DATABASE LOAD RESULT
    private static class DashboardData {

        private final List<StudentRow> students;
        private final List<ProgramRow> programs;
        private final List<String> programNames;

        private DashboardData(
                List<StudentRow> students,
                List<ProgramRow> programs,
                List<String> programNames
        ) {

            this.students = students;
            this.programs = programs;
            this.programNames = programNames;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
