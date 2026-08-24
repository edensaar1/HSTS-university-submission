package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.StudentResultsClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.StudentResultView;
import il.cshaifasweng.hsts.entities.view.SubmissionReviewAnswerView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentResultsScreen {
    private StudentResultsClientLogic studentResultsClientLogic;

    @FXML private TableView<StudentResultView> resultsTable;
    @FXML private TableColumn<StudentResultView, String> examIdColumn;
    @FXML private TableColumn<StudentResultView, String> courseColumn;
    @FXML private TableColumn<StudentResultView, LocalDateTime> submittedAtColumn;
    @FXML private TableColumn<StudentResultView, String> completionStatusColumn;
    @FXML private TableColumn<StudentResultView, Number> finalGradeColumn;

    @FXML private TableView<SubmissionReviewAnswerView> answersTable;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> questionIdColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> questionTextColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> selectedAnswerColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> correctAnswerColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, Number> pointsColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> resultColumn;

    @FXML private Label selectedResultLabel;
    @FXML private TextArea teacherCommentArea;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        studentResultsClientLogic = new StudentResultsClientLogic();
        EventBus.getDefault().register(this);

        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("examId"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        completionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("completionStatus"));
        finalGradeColumn.setCellValueFactory(new PropertyValueFactory<>("finalGrade"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        submittedAtColumn.setCellFactory(column -> new TableCell<StudentResultView, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime submittedAt, boolean empty) {
                super.updateItem(submittedAt, empty);
                setText(empty || submittedAt == null ? "" : formatter.format(submittedAt));
            }
        });

        questionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));
        selectedAnswerColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                getAnswerText(cell.getValue(), cell.getValue().getSelectedAnswer())));
        correctAnswerColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                getAnswerText(cell.getValue(), cell.getValue().getCorrectAnswer())));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        resultColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getSelectedAnswer() == cell.getValue().getCorrectAnswer()
                        ? "Correct" : "Incorrect"));

        resultsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldResult, selectedResult) -> showResult(selectedResult));
        requestResults();
    }

    private void showResult(StudentResultView result) {
        if(result == null){
            selectedResultLabel.setText("Select a result");
            answersTable.getItems().clear();
            teacherCommentArea.clear();
            return;
        }

        selectedResultLabel.setText(
                "Exam " + result.getExamId() + " - Final Grade: " + result.getFinalGrade());
        answersTable.getItems().setAll(result.getAnswers());
        teacherCommentArea.setText(result.getTeacherComment() == null
                ? "" : result.getTeacherComment());
    }

    private String getAnswerText(SubmissionReviewAnswerView answer, int answerNumber) {
        switch(answerNumber){
            case 1:
                return "1 - " + answer.getAnswer1();
            case 2:
                return "2 - " + answer.getAnswer2();
            case 3:
                return "3 - " + answer.getAnswer3();
            case 4:
                return "4 - " + answer.getAnswer4();
            default:
                return "";
        }
    }

    @FXML
    private void handleRefresh() {
        resultsTable.getSelectionModel().clearSelection();
        requestResults();
    }

    private void requestResults() {
        statusLabel.setText("Loading results...");
        try {
            studentResultsClientLogic.requestStudentResults();
        } catch (RuntimeException e) {
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
            switch(message.getType()){
                case GET_STUDENT_RESULTS_RESPONSE:
                    List<StudentResultView> results =
                            (List<StudentResultView>) message.getPayload();
                    resultsTable.getItems().setAll(results);
                    resultsTable.getSelectionModel().clearSelection();
                    statusLabel.setText(results.size() + " approved result(s) loaded");
                    break;
                case ERROR:
                    statusLabel.setText(String.valueOf(message.getPayload()));
                    break;
                default:
                    break;
            }
        });
    }

    private void shutdown() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
