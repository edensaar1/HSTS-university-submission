package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.ExamSubmissionManagementClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.ExamSubmissionView;
import il.cshaifasweng.hsts.entities.view.SubmissionReviewAnswerView;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExamSubmissionManagementScreen {
    private ExamSubmissionManagementClientLogic examSubmissionManagementClientLogic;
    private String statusAfterRefresh;

    @FXML private TableView<ExamSubmissionView> submissionsTable;
    @FXML private TableColumn<ExamSubmissionView, String> studentColumn;
    @FXML private TableColumn<ExamSubmissionView, String> examIdColumn;
    @FXML private TableColumn<ExamSubmissionView, String> courseColumn;
    @FXML private TableColumn<ExamSubmissionView, LocalDateTime> submittedAtColumn;
    @FXML private TableColumn<ExamSubmissionView, Number> automaticGradeColumn;
    @FXML private TableColumn<ExamSubmissionView, Number> finalGradeColumn;

    @FXML private TableView<SubmissionReviewAnswerView> answersTable;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> questionIdColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> questionTextColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> selectedAnswerColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> correctAnswerColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, Number> pointsColumn;
    @FXML private TableColumn<SubmissionReviewAnswerView, String> resultColumn;

    @FXML private Label selectedSubmissionLabel;
    @FXML private TextField finalGradeField;
    @FXML private TextArea teacherCommentArea;
    @FXML private TextArea gradeChangeReasonArea;
    @FXML private Button approveButton;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        examSubmissionManagementClientLogic = new ExamSubmissionManagementClientLogic();
        EventBus.getDefault().register(this);

        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("examId"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        submittedAtColumn.setCellValueFactory(new PropertyValueFactory<>("submittedAt"));
        automaticGradeColumn.setCellValueFactory(new PropertyValueFactory<>("automaticGrade"));
        finalGradeColumn.setCellValueFactory(new PropertyValueFactory<>("finalGrade"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        submittedAtColumn.setCellFactory(column -> new TableCell<ExamSubmissionView, LocalDateTime>() {
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

        approveButton.setDisable(true);
        submissionsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSubmission, selectedSubmission) -> showSubmission(selectedSubmission));

        requestSubmissions();
    }

    private void showSubmission(ExamSubmissionView submission) {
        if(submission == null){
            selectedSubmissionLabel.setText("Select a submission");
            answersTable.getItems().clear();
            finalGradeField.clear();
            teacherCommentArea.clear();
            gradeChangeReasonArea.clear();
            approveButton.setDisable(true);
            return;
        }

        selectedSubmissionLabel.setText(
                submission.getStudentName() + " - Exam " + submission.getExamId());
        answersTable.getItems().setAll(submission.getAnswers());
        finalGradeField.setText(submission.getFinalGrade() == null
                ? "" : String.valueOf(submission.getFinalGrade()));
        teacherCommentArea.setText(submission.getTeacherComment() == null
                ? "" : submission.getTeacherComment());
        gradeChangeReasonArea.setText(submission.getGradeChangeReason() == null
                ? "" : submission.getGradeChangeReason());
        approveButton.setDisable(false);
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
        submissionsTable.getSelectionModel().clearSelection();
        requestSubmissions();
    }

    private void requestSubmissions() {
        statusLabel.setText("Loading submissions...");
        try {
            examSubmissionManagementClientLogic.requestSubmissionsForReview();
        } catch (RuntimeException e) {
            statusLabel.setText("Could not contact the server");
        }
    }

    @FXML
    private void handleApprove(){
        ExamSubmissionView selectedSubmissionView = submissionsTable.getSelectionModel().getSelectedItem();
        if(selectedSubmissionView == null){
            statusLabel.setText("Please select a submission");
            return;
        }
        int finalGrade;
        try {
            finalGrade = Integer.parseInt(finalGradeField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Final grade must be a whole number");
            return;
        }
        if(finalGrade < 0 || finalGrade > 100){
            statusLabel.setText("Final grade must be between 0 and 100");
            return;
        }
        if(selectedSubmissionView.getAutomaticGrade() == null){
            statusLabel.setText("Automatic grade is missing");
            return;
        }
        String gradeChangeReason = gradeChangeReasonArea.getText().trim();
        if(finalGrade != selectedSubmissionView.getAutomaticGrade() && gradeChangeReason.isBlank()){
            statusLabel.setText("A reason is required when changing the automatic grade");
            return;
        }
        approveButton.setDisable(true);
        statusLabel.setText("Approving submission...");
        try {
            examSubmissionManagementClientLogic.approveSubmission(selectedSubmissionView.getSubmissionId(), finalGrade,
                    teacherCommentArea.getText().trim(), gradeChangeReason);
        } catch (RuntimeException e) {
            approveButton.setDisable(false);
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
                case GET_SUBMISSIONS_FOR_REVIEW_RESPONSE:
                    List<ExamSubmissionView> submissions =
                            (List<ExamSubmissionView>) message.getPayload();
                    submissionsTable.getItems().setAll(submissions);
                    submissionsTable.getSelectionModel().clearSelection();
                    if(statusAfterRefresh != null){
                        statusLabel.setText(statusAfterRefresh);
                        statusAfterRefresh = null;
                    } else {
                        statusLabel.setText(submissions.size() + " submission(s) awaiting review");
                    }
                    break;
                case APPROVE_SUBMISSION_RESPONSE:
                    boolean approved = (boolean) message.getPayload();
                    if(approved){
                        statusAfterRefresh = "Submission approved successfully";
                        submissionsTable.getSelectionModel().clearSelection();
                        requestSubmissions();
                    }
                    else{
                        statusLabel.setText("Could not approve submission");
                        approveButton.setDisable(
                                submissionsTable.getSelectionModel().getSelectedItem() == null);
                    }
                    break;
                case ERROR:
                    statusLabel.setText(String.valueOf(message.getPayload()));
                    approveButton.setDisable(
                            submissionsTable.getSelectionModel().getSelectedItem() == null);
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
