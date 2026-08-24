package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.ExamApprovalClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.ExamView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class ExamApprovalScreen {
    private ExamApprovalClientLogic examApprovalClientLogic;
    private String statusAfterRefresh;

    @FXML private TableView<ExamView> pendingExamsTable;
    @FXML private TableColumn<ExamView, String> examIdColumn;
    @FXML private TableColumn<ExamView, String> courseColumn;
    @FXML private TableColumn<ExamView, String> teacherColumn;
    @FXML private TableColumn<ExamView, Number> durationColumn;
    @FXML private Label selectedExamLabel;
    @FXML private TextArea studentInstructionsArea;
    @FXML private TextArea teacherInstructionsArea;
    @FXML private TextArea rejectionReasonArea;
    @FXML private Label statusLabel;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;

    @FXML private void initialize() {
        examApprovalClientLogic = new ExamApprovalClientLogic();
        EventBus.getDefault().register(this);

        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("examId"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        approveButton.setDisable(true);
        rejectButton.setDisable(true);

        pendingExamsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldExam, selectedExam) -> {
                    boolean noSelection = selectedExam == null;
                    approveButton.setDisable(noSelection);
                    rejectButton.setDisable(noSelection);

                    if (noSelection) {
                        selectedExamLabel.setText("Select an exam");
                        studentInstructionsArea.clear();
                        teacherInstructionsArea.clear();
                        rejectionReasonArea.clear();
                        return;
                    }

                    selectedExamLabel.setText("Exam " + selectedExam.getExamId());
                    studentInstructionsArea.setText(selectedExam.getStudentInstructions());
                    teacherInstructionsArea.setText(selectedExam.getTeacherInstructions());
                    rejectionReasonArea.clear();
                });

        examApprovalClientLogic.requestPendingExams();
    }
    @FXML private void handleRefresh() {
        statusLabel.setText("");
        pendingExamsTable.getSelectionModel().clearSelection();
        examApprovalClientLogic.requestPendingExams();
    }

    @FXML private void handleApprove(){
        ExamView examView = pendingExamsTable.getSelectionModel().getSelectedItem();
        if(examView == null){
            statusLabel.setText("Please select an exam");
        }
        else{
            approveButton.setDisable(true);
            rejectButton.setDisable(true);
            examApprovalClientLogic.approveExam(examView.getExamId());
        }
    }

    @FXML private void handleReject() {
        ExamView examView = pendingExamsTable.getSelectionModel().getSelectedItem();
        if(examView == null){
            statusLabel.setText("Please select an exam");
        }
        else{
            String rejectionReason = rejectionReasonArea.getText().trim();
            if(rejectionReason.isBlank()){
                statusLabel.setText("Rejection reason is required");
                return;
            }
            approveButton.setDisable(true);
            rejectButton.setDisable(true);
            examApprovalClientLogic.rejectExam(examView.getExamId(), rejectionReason);
        }
    }

    @FXML private void handleBack() {
        try{
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
                case GET_PENDING_EXAMS_RESPONSE:
                    List<ExamView> examViews = (List<ExamView>) message.getPayload();
                    pendingExamsTable.getItems().setAll(examViews);
                    pendingExamsTable.getSelectionModel().clearSelection();
                    if (statusAfterRefresh != null) {
                        statusLabel.setText(statusAfterRefresh);
                        statusAfterRefresh = null;
                    } else {
                        statusLabel.setText(examViews.size() + " pending exam(s) loaded");
                    }
                    break;
                case APPROVE_EXAM_RESPONSE:
                    boolean approved = (boolean) message.getPayload();
                    if(approved){
                        statusAfterRefresh = "Exam approved successfully";
                        examApprovalClientLogic.requestPendingExams();
                    }
                    else{
                        statusLabel.setText("Could not approve exam");
                        boolean examSelectedAfterApprovalFailure =
                                pendingExamsTable.getSelectionModel().getSelectedItem() != null;
                        approveButton.setDisable(!examSelectedAfterApprovalFailure);
                        rejectButton.setDisable(!examSelectedAfterApprovalFailure);
                    }
                    break;

                case REJECT_EXAM_RESPONSE:
                    boolean rejected = (boolean) message.getPayload();
                    if (rejected) {
                        statusAfterRefresh = "Exam rejected successfully";
                        rejectionReasonArea.clear();
                        examApprovalClientLogic.requestPendingExams();
                    } else {
                        statusLabel.setText("Could not reject exam");
                        boolean examSelectedAfterRejectionFailure =
                                pendingExamsTable.getSelectionModel().getSelectedItem() != null;
                        approveButton.setDisable(!examSelectedAfterRejectionFailure);
                        rejectButton.setDisable(!examSelectedAfterRejectionFailure);
                    }
                    break;

                case ERROR:
                    statusLabel.setText((String) message.getPayload());
                    boolean examSelectedAfterError =
                            pendingExamsTable.getSelectionModel().getSelectedItem() != null;
                    approveButton.setDisable(!examSelectedAfterError);
                    rejectButton.setDisable(!examSelectedAfterError);
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
