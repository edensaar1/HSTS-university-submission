package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.ExamExecutionClientLogic;
import il.cshaifasweng.hsts.client.state.ClientSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.ExamExecutionView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class ExamEntryScreen {
    private ExamExecutionClientLogic examExecutionClientLogic;

    @FXML private TextField executionCodeField;
    @FXML private TextField studentIdField;
    @FXML private Button startExamButton;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        examExecutionClientLogic = new ExamExecutionClientLogic();
        EventBus.getDefault().register(this);
    }

    @FXML
    private void handleStartExam() {
        String executionCode = executionCodeField.getText().trim();
        String studentId = studentIdField.getText().trim();
        if(!executionCode.matches("[A-Za-z0-9]{4}")){
            statusLabel.setText("Execution code must contain exactly four letters or digits");
            return;
        }
        if(studentId.isBlank()){
            statusLabel.setText("Student ID is required");
            return;
        }

        startExamButton.setDisable(true);
        statusLabel.setText("Starting exam...");
        try {
            examExecutionClientLogic.startExam(executionCode, studentId);
        } catch (RuntimeException e) {
            startExamButton.setDisable(false);
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
                case START_EXAM_RESPONSE:
                    ExamExecutionView examExecutionView = (ExamExecutionView) message.getPayload();
                    ClientSession.setActiveExam(examExecutionView);
                    try {
                        App.showExamExecution();
                        shutdown();
                    } catch (IOException e) {
                        statusLabel.setText("Could not open the exam screen");
                        startExamButton.setDisable(false);
                    }
                    break;

                case ERROR:
                    statusLabel.setText(String.valueOf(message.getPayload()));
                    startExamButton.setDisable(false);
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
