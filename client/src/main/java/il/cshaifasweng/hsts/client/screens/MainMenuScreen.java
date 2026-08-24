package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.LoginClientLogic;
import il.cshaifasweng.hsts.client.state.ClientSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.view.CurrentSessionView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class MainMenuScreen {
    private LoginClientLogic loginClientLogic;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button questionBankButton;

    @FXML
    private Button examCreationButton;

    @FXML
    private Button examManagementButton;

    @FXML
    private Button submissionManagementButton;

    @FXML
    private Button teacherStatisticsButton;

    @FXML
    private Button examApprovalButton;

    @FXML
    private Button examEntryButton;

    @FXML
    private Button studentResultsButton;

    @FXML
    private Button logoutButton;



    @FXML
    private void initialize() {
        loginClientLogic = new LoginClientLogic();
        EventBus.getDefault().register(this);

        CurrentSessionView sessionView = ClientSession.getCurrentSession();
        if(sessionView == null){
            Platform.runLater(() -> {
                try {
                    App.showLogin();
                    shutdown();
                } catch (IOException e) {
                    welcomeLabel.setText("Could not open the login screen");
                }
            });
            return;
        }

        welcomeLabel.setText("Welcome, " + sessionView.getFullName());
        roleLabel.setText("Role: " + sessionView.getRole());
        boolean teacher = "Teacher".equals(sessionView.getRole());
        boolean subjectCoordinator = "SubjectCoordinator".equals(sessionView.getRole());
        boolean student = "Student".equals(sessionView.getRole());

        questionBankButton.setVisible(teacher);
        questionBankButton.setManaged(teacher);
        examCreationButton.setVisible(teacher);
        examCreationButton.setManaged(teacher);
        examManagementButton.setVisible(teacher);
        examManagementButton.setManaged(teacher);
        submissionManagementButton.setVisible(teacher);
        submissionManagementButton.setManaged(teacher);
        teacherStatisticsButton.setVisible(teacher);
        teacherStatisticsButton.setManaged(teacher);
        examApprovalButton.setVisible(subjectCoordinator);
        examApprovalButton.setManaged(subjectCoordinator);
        examEntryButton.setVisible(student);
        examEntryButton.setManaged(student);
        studentResultsButton.setVisible(student);
        studentResultsButton.setManaged(student);
    }

    @FXML
    private void handleQuestionBank() {
        try {
            App.showQuestionBank();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open the question bank");
        }
    }

    @FXML
    private void handleExamCreation() {
        try {
            App.showExamCreation();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open exam creation");
        }
    }

    @FXML
    private void handleExamManagement() {
        try {
            App.showExamManagement();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open exam management");
        }
    }

    @FXML
    private void handleSubmissionManagement() {
        try {
            App.showSubmissionManagement();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open submission review");
        }
    }

    @FXML
    private void handleTeacherStatistics() {
        try {
            App.showTeacherStatistics();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open exam statistics");
        }
    }

    @FXML
    private void handleExamApproval() {
        try {
            App.showExamApproval();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open exam approval");
        }
    }

    @FXML
    private void handleExamEntry() {
        try {
            App.showExamEntry();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open exam entry");
        }
    }

    @FXML
    private void handleStudentResults() {
        try {
            App.showStudentResults();
            shutdown();
        } catch (IOException e) {
            welcomeLabel.setText("Could not open student results");
        }
    }

    @FXML
    private void handleLogout() {
        logoutButton.setDisable(true);
        try {
            loginClientLogic.requestLogout();
        } catch (RuntimeException e) {
            welcomeLabel.setText("Could not contact the server");
            logoutButton.setDisable(false);
        }
    }

    @Subscribe
    public void onHSTSMessage(HSTSMessage message) {
        Platform.runLater(() -> {
            MessageType type = message.getType();
            switch (type) {
                case LOGOUT_RESPONSE:
                    ClientSession.clear();
                    try {
                        App.showLogin();
                        shutdown();
                    } catch (IOException e) {
                        welcomeLabel.setText("Could not open the login screen");
                        logoutButton.setDisable(false);
                    }
                    break;

                case ERROR:
                    welcomeLabel.setText(String.valueOf(message.getPayload()));
                    logoutButton.setDisable(false);
                    break;

                default:
                    break;
            }
        });
    }

    public void shutdown() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
