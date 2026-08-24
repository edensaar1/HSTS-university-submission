package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.ExamManagementClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.ExamInstanceView;
import il.cshaifasweng.hsts.entities.view.ExamView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ExamManagementScreen {
    private ExamManagementClientLogic examManagementClientLogic;
    private String statusAfterRefresh;

    @FXML private ComboBox<ExamView> approvedExamComboBox;
    @FXML private DatePicker openingDatePicker;
    @FXML private TextField openingTimeField;
    @FXML private DatePicker closingDatePicker;
    @FXML private TextField closingTimeField;
    @FXML private TextField executionCodeField;
    @FXML private Button scheduleButton;

    @FXML private TableView<ExamInstanceView> examInstancesTable;
    @FXML private TableColumn<ExamInstanceView, Number> instanceIdColumn;
    @FXML private TableColumn<ExamInstanceView, String> examIdColumn;
    @FXML private TableColumn<ExamInstanceView, String> courseColumn;
    @FXML private TableColumn<ExamInstanceView, LocalDateTime> openingColumn;
    @FXML private TableColumn<ExamInstanceView, LocalDateTime> closingColumn;
    @FXML private TableColumn<ExamInstanceView, String> executionCodeColumn;
    @FXML private TableColumn<ExamInstanceView, Number> effectiveDurationColumn;

    @FXML private TextField additionalMinutesField;
    @FXML private Button extendDurationButton;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        examManagementClientLogic = new ExamManagementClientLogic();
        EventBus.getDefault().register(this);

        approvedExamComboBox.setConverter(new StringConverter<ExamView>() {
            @Override
            public String toString(ExamView exam) {
                if (exam == null) {
                    return "";
                }
                return exam.getExamId() + " - " + exam.getCourseName() +
                        " - authored by " + exam.getTeacherName();
            }

            @Override
            public ExamView fromString(String text) {
                return null;
            }
        });

        instanceIdColumn.setCellValueFactory(new PropertyValueFactory<>("instanceId"));
        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("examId"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        openingColumn.setCellValueFactory(new PropertyValueFactory<>("openingTime"));
        closingColumn.setCellValueFactory(new PropertyValueFactory<>("closingTime"));
        executionCodeColumn.setCellValueFactory(new PropertyValueFactory<>("executionCode"));
        effectiveDurationColumn.setCellValueFactory(
                new PropertyValueFactory<>("effectiveDuration"));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        openingColumn.setCellFactory(column ->
                new TableCell<ExamInstanceView, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? "" : dateTimeFormatter.format(value));
            }
        });
        closingColumn.setCellFactory(column ->
                new TableCell<ExamInstanceView, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? "" : dateTimeFormatter.format(value));
            }
        });

        extendDurationButton.setDisable(true);
        examInstancesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldInstance,
                 selectedInstance) -> extendDurationButton.setDisable(selectedInstance == null));

        examManagementClientLogic.requestApprovedExams();
        examManagementClientLogic.requestTeacherExamInstances();
    }





    @FXML
    private void handleScheduleExam() {
        ExamView selectedExam = approvedExamComboBox.getValue();
        if (selectedExam == null) {
            statusLabel.setText("Please select an approved exam");
            return;
        }
        if (openingDatePicker.getValue() == null ||
                closingDatePicker.getValue() == null) {
            statusLabel.setText("Opening and closing dates are required");
            return;
        }

        LocalDateTime openingTime;
        LocalDateTime closingTime;
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            openingTime = LocalDateTime.of(openingDatePicker.getValue(),
                    LocalTime.parse(openingTimeField.getText().trim(), timeFormatter));

            closingTime = LocalDateTime.of(closingDatePicker.getValue(),
                    LocalTime.parse(closingTimeField.getText().trim(), timeFormatter));

        } catch (DateTimeParseException e) {
            statusLabel.setText("Times must use HH:mm format");
            return;
        }

        if (!closingTime.isAfter(openingTime)) {
            statusLabel.setText("Closing time must be after opening time");
            return;
        }
        LocalDateTime currentMinute = LocalDateTime.now().withSecond(0).withNano(0);
        if (openingTime.isBefore(currentMinute)) {
            statusLabel.setText("Opening time cannot be in the past");
            return;
        }

        String executionCode = executionCodeField.getText().trim();
        if (!executionCode.matches("[A-Za-z0-9]{4}")) {
            statusLabel.setText("Execution code must contain exactly four letters or digits");
            return;
        }

        ExamInstanceView instanceView = new ExamInstanceView(
                selectedExam.getExamId(),
                openingTime,
                closingTime,
                executionCode);

        scheduleButton.setDisable(true);
        statusLabel.setText("Scheduling exam...");
        try {
            examManagementClientLogic.scheduleExam(instanceView);
        } catch (RuntimeException e) {
            scheduleButton.setDisable(false);
            statusLabel.setText("Could not contact the server");
        }
    }

    @FXML
    private void handleExtendDuration() {
        ExamInstanceView selectedInstance = examInstancesTable.getSelectionModel().getSelectedItem();
        if (selectedInstance == null) {
            statusLabel.setText("Please select an exam instance");
            return;
        }

        int additionalMinutes;
        try {
            additionalMinutes = Integer.parseInt(additionalMinutesField.getText().trim());
        }
        catch (NumberFormatException e) {
            statusLabel.setText("Additional minutes must be a whole number");
            return;
        }
        if (additionalMinutes <= 0) {
            statusLabel.setText("Additional minutes must be positive");
            return;
        }

        extendDurationButton.setDisable(true);
        statusLabel.setText("Extending exam duration...");
        try {
            examManagementClientLogic.extendExamDuration(selectedInstance.getInstanceId(), additionalMinutes);
        }
        catch (RuntimeException e) {
            extendDurationButton.setDisable(false);
            statusLabel.setText("Could not contact the server");
        }
    }

    @FXML
    private void handleRefresh() {
        statusLabel.setText("Refreshing...");
        examInstancesTable.getSelectionModel().clearSelection();
        try {
            examManagementClientLogic.requestApprovedExams();
            examManagementClientLogic.requestTeacherExamInstances();
        }
        catch (RuntimeException e) {
            statusLabel.setText("Could not contact the server");
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.showMainMenu();
            shutdown();
        } catch (IOException e) {
            statusLabel.setText("Could not return to main menu");
        }
    }

    @Subscribe
    public void onServerMessage(HSTSMessage message) {
        Platform.runLater(() -> {
            switch (message.getType()) {
                case GET_APPROVED_EXAMS_RESPONSE:
                    List<ExamView> approvedExams =
                            (List<ExamView>) message.getPayload();
                    approvedExamComboBox.getItems().setAll(approvedExams);
                    if (!approvedExams.isEmpty() &&
                            approvedExamComboBox.getValue() == null) {
                        approvedExamComboBox.getSelectionModel().selectFirst();
                    }
                    break;

                case GET_TEACHER_EXAM_INSTANCES_RESPONSE:
                    List<ExamInstanceView> examInstances =
                            (List<ExamInstanceView>) message.getPayload();
                    examInstancesTable.getItems().setAll(examInstances);
                    examInstancesTable.getSelectionModel().clearSelection();
                    if (statusAfterRefresh != null) {
                        statusLabel.setText(statusAfterRefresh);
                        statusAfterRefresh = null;
                    } else {
                        statusLabel.setText(
                                examInstances.size() + " scheduled instance(s) loaded");
                    }
                    break;

                case SCHEDULE_EXAM_RESPONSE:
                    boolean scheduled = (boolean) message.getPayload();
                    if (scheduled) {
                        statusAfterRefresh = "Exam scheduled successfully";
                        scheduleButton.setDisable(false);
                        openingDatePicker.setValue(null);
                        openingTimeField.clear();
                        closingDatePicker.setValue(null);
                        closingTimeField.clear();
                        executionCodeField.clear();
                        examManagementClientLogic.requestTeacherExamInstances();
                    } else {
                        statusLabel.setText("Could not schedule exam");
                        scheduleButton.setDisable(false);
                    }
                    break;

                case EXTEND_EXAM_DURATION_RESPONSE:
                    boolean extended = (boolean) message.getPayload();
                    if (extended) {
                        statusAfterRefresh = "Exam duration extended successfully";
                        additionalMinutesField.clear();
                        examManagementClientLogic.requestTeacherExamInstances();
                    } else {
                        statusLabel.setText("Could not extend exam duration");
                        extendDurationButton.setDisable(
                                examInstancesTable.getSelectionModel()
                                        .getSelectedItem() == null);
                    }
                    break;

                case ERROR:
                    statusLabel.setText(String.valueOf(message.getPayload()));
                    scheduleButton.setDisable(false);
                    extendDurationButton.setDisable(
                            examInstancesTable.getSelectionModel()
                                    .getSelectedItem() == null);
                    break;

                default:
                    break;
            }
        });
    }

    private void shutdown() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
