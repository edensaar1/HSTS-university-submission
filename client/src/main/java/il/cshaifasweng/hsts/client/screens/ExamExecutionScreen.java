package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.ExamExecutionClientLogic;
import il.cshaifasweng.hsts.client.state.ClientSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.request.ExtendExamDurationRequest;
import il.cshaifasweng.hsts.entities.view.ExamExecutionView;
import il.cshaifasweng.hsts.entities.view.StudentQuestionView;
import il.cshaifasweng.hsts.entities.view.SubmissionAnswerView;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class ExamExecutionScreen {
    private ExamExecutionClientLogic examExecutionClientLogic;
    private ExamExecutionView activeExam;
    private List<Integer> selectedAnswers;
    private int currentQuestionIndex;
    private Timeline countdownTimeline;
    private LocalDateTime deadline;
    private boolean timeoutRequested;
    private boolean extensionReceivedWhileTimeoutPending;
    private boolean timeoutRejectedWhilePending;
    private boolean submissionRequested;
    private boolean closeAfterSubmission;


    @FXML private Label examTitleLabel;
    @FXML private Label timerLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Label pointsLabel;
    @FXML private Label questionTextLabel;

    @FXML private RadioButton answer1Radio;
    @FXML private RadioButton answer2Radio;
    @FXML private RadioButton answer3Radio;
    @FXML private RadioButton answer4Radio;
    @FXML private ToggleGroup answersToggleGroup;

    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        examExecutionClientLogic = new ExamExecutionClientLogic();
        activeExam = ClientSession.getActiveExam();
        if(activeExam == null || activeExam.getQuestions() == null || activeExam.getQuestions().isEmpty()){
            statusLabel.setText("No active exam is available");
            previousButton.setDisable(true);
            nextButton.setDisable(true);
            submitButton.setDisable(true);
            return;
        }

        selectedAnswers = new ArrayList<>();
        for(int i = 0; i < activeExam.getQuestions().size(); i++){
            selectedAnswers.add(0);
        }
        currentQuestionIndex = 0;
        EventBus.getDefault().register(this);
        App.setActiveExamCloseHandler(this::handleWindowClose);
        examTitleLabel.setText(activeExam.getCourseName() + " - Exam " + activeExam.getExamId());
        showCurrentQuestion();
        startTimer();
    }

    private void startTimer() {
        deadline = activeExam.getStartedAt().plusMinutes(activeExam.getDurationMinutes());
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimer();
        countdownTimeline.play();
    }

    private void stopTimer() {
        if(countdownTimeline != null){
            countdownTimeline.stop();
        }
    }

    private void updateTimer() {
        long remainingMillis = java.time.Duration.between(LocalDateTime.now(), deadline).toMillis();
        if(remainingMillis <= 0){
            timerLabel.setText("00:00");
            stopTimer();
            if(!timeoutRequested){
                handleTimeout();
            }
        }
        else{
            long remainingSeconds = (remainingMillis + 999) / 1000;
            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }



    }

    private void showCurrentQuestion() {
        StudentQuestionView question = activeExam.getQuestions().get(currentQuestionIndex);
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + activeExam.getQuestions().size());
        pointsLabel.setText(question.getPoints() + " points");
        questionTextLabel.setText(question.getText());
        answer1Radio.setText(question.getAnswer1());
        answer2Radio.setText(question.getAnswer2());
        answer3Radio.setText(question.getAnswer3());
        answer4Radio.setText(question.getAnswer4());

        answersToggleGroup.selectToggle(null);
        int selectedAnswer = selectedAnswers.get(currentQuestionIndex);
        if(selectedAnswer == 1){
            answer1Radio.setSelected(true);
        } else if(selectedAnswer == 2){
            answer2Radio.setSelected(true);
        } else if(selectedAnswer == 3){
            answer3Radio.setSelected(true);
        } else if(selectedAnswer == 4){
            answer4Radio.setSelected(true);
        }

        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == activeExam.getQuestions().size() - 1);
    }

    private void saveCurrentAnswer(){
        int selectedAnswerIndex = 0;
        if(answer1Radio.isSelected()){
            selectedAnswerIndex = 1;
        }
        else if(answer2Radio.isSelected()){
            selectedAnswerIndex = 2;
        }
        else if(answer3Radio.isSelected()){
            selectedAnswerIndex = 3;
        }
        else if(answer4Radio.isSelected()){
            selectedAnswerIndex = 4;
        }
        selectedAnswers.set(currentQuestionIndex, selectedAnswerIndex);
    }


    private List<SubmissionAnswerView> buildAnswerViews(){
        saveCurrentAnswer();
        List<SubmissionAnswerView> submissionAnswerViews = new ArrayList<>();
        for(int ind = 0; ind < selectedAnswers.size(); ind++){
            if(selectedAnswers.get(ind) == 0){
                continue;
            }
            StudentQuestionView studentQuestionView = activeExam.getQuestions().get(ind);
            SubmissionAnswerView submissionAnswerView = new SubmissionAnswerView(
                    studentQuestionView.getExamQuestionId(), selectedAnswers.get(ind));
            submissionAnswerViews.add(submissionAnswerView);
        }
        return submissionAnswerViews;
    }

    private void setExamControlsDisabled(boolean disabled) {
        answer1Radio.setDisable(disabled);
        answer2Radio.setDisable(disabled);
        answer3Radio.setDisable(disabled);
        answer4Radio.setDisable(disabled);
        submitButton.setDisable(disabled);
        if(disabled){
            previousButton.setDisable(true);
            nextButton.setDisable(true);
        }
        else{
            previousButton.setDisable(currentQuestionIndex == 0);
            nextButton.setDisable(currentQuestionIndex == activeExam.getQuestions().size() - 1);
        }
    }

    private void handleTimeout() {
        timeoutRequested = true;
        List<SubmissionAnswerView> answerViews = buildAnswerViews();
        setExamControlsDisabled(true);
        statusLabel.setText("Time is up. Submitting exam...");
        try{
            examExecutionClientLogic.timeoutExam(activeExam.getSubmissionId(), answerViews);
        }
        catch(RuntimeException e){
            statusLabel.setText("Time is up, but the server could not be contacted");
        }
    }

    private void resumeAfterRejectedTimeout() {
        timeoutRequested = false;
        extensionReceivedWhileTimeoutPending = false;
        timeoutRejectedWhilePending = false;
        setExamControlsDisabled(false);
        updateTimer();
        countdownTimeline.playFromStart();
    }







    @FXML
    private void handlePrevious() {
        saveCurrentAnswer();
        if(currentQuestionIndex > 0){
            currentQuestionIndex--;
            showCurrentQuestion();
        }
    }

    @FXML
    private void handleNext() {
        saveCurrentAnswer();
        if(currentQuestionIndex < selectedAnswers.size() - 1){
            currentQuestionIndex++;
            showCurrentQuestion();
        }
    }

    @FXML
    private void handleSubmit() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Submit Exam");
        confirmation.setHeaderText("Submit this exam?");
        confirmation.setContentText("Submission is final. Unanswered questions will remain unanswered.");
        if(confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK){
            return;
        }

        closeAfterSubmission = false;
        submitCurrentAnswers("Submitting exam...");
    }

    private void handleWindowClose(){
        if(timeoutRequested || submissionRequested){
            statusLabel.setText("Please wait while the exam is being submitted");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Close Exam");
        confirmation.setHeaderText("Submit the exam and close the application?");
        confirmation.setContentText("Your current answers will be submitted. This action is final.");
        if(confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK){
            return;
        }

        closeAfterSubmission = true;
        submitCurrentAnswers("Submitting exam before closing...");
    }

    private void submitCurrentAnswers(String statusMessage){
        submissionRequested = true;
        List<SubmissionAnswerView> answerViews = buildAnswerViews();
        setExamControlsDisabled(true);
        stopTimer();
        statusLabel.setText(statusMessage);
        try{
            examExecutionClientLogic.submitExam(activeExam.getSubmissionId(), answerViews);
        }
        catch(RuntimeException e){
            submissionRequested = false;
            closeAfterSubmission = false;
            statusLabel.setText("Could not contact the server");
            setExamControlsDisabled(false);
            countdownTimeline.play();
        }
    }

    @Subscribe
    public void onServerMessage(HSTSMessage message) {
        Platform.runLater(() -> {
            switch(message.getType()){
                case SUBMIT_EXAM_RESPONSE:
                    boolean submitted = (boolean) message.getPayload();
                    if(submitted){
                        ClientSession.setActiveExam(null);
                        shutdown();
                        if(closeAfterSubmission){
                            App.closeAfterExamSubmission();
                        }
                        else{
                            try{
                                App.showMainMenu();
                            }
                            catch(IOException e){
                                statusLabel.setText("Exam submitted, but the main menu could not be opened");
                            }
                        }
                    }
                    else{
                        submissionRequested = false;
                        closeAfterSubmission = false;
                        statusLabel.setText("Could not submit the exam");
                        setExamControlsDisabled(false);
                        countdownTimeline.play();
                    }
                    break;

                case TIMEOUT_EXAM_RESPONSE:
                    boolean timedOut = (boolean) message.getPayload();
                    if(timedOut){
                        ClientSession.setActiveExam(null);
                        try{
                            App.showMainMenu();
                            shutdown();
                        }
                        catch(IOException e){
                            statusLabel.setText("Exam closed, but the main menu could not be opened");
                        }
                    }
                    else{
                        statusLabel.setText("Time is up, but the exam could not be submitted");
                    }
                    break;

                case EXAM_DURATION_EXTENDED_NOTIFICATION:
                    ExtendExamDurationRequest extensionRequest = (ExtendExamDurationRequest) message.getPayload();
                    if(extensionRequest != null && activeExam.getInstanceId() != null &&
                            activeExam.getInstanceId().equals(extensionRequest.getInstanceId())){
                        deadline = deadline.plusMinutes(extensionRequest.getAdditionalMinutes());
                        statusLabel.setText("Teacher added " + extensionRequest.getAdditionalMinutes() + " minute(s)");
                        if(timeoutRequested){
                            extensionReceivedWhileTimeoutPending = true;
                            if(timeoutRejectedWhilePending){
                                resumeAfterRejectedTimeout();
                            }
                        }
                        else{
                            updateTimer();
                        }
                    }
                    break;

                case ERROR:
                    String errorMessage = String.valueOf(message.getPayload());
                    statusLabel.setText(errorMessage);
                    if(timeoutRequested){
                        timeoutRejectedWhilePending = true;
                        if(extensionReceivedWhileTimeoutPending){
                            resumeAfterRejectedTimeout();
                        }
                        else if("Exam time has not expired".equals(errorMessage)){
                            PauseTransition timeoutRetry = new PauseTransition(Duration.seconds(1));
                            timeoutRetry.setOnFinished(event -> {
                                if(timeoutRequested && !extensionReceivedWhileTimeoutPending){
                                    timeoutRequested = false;
                                    timeoutRejectedWhilePending = false;
                                    handleTimeout();
                                }
                            });
                            timeoutRetry.play();
                        }
                    }
                    else if(!timeoutRequested){
                        submissionRequested = false;
                        closeAfterSubmission = false;
                        setExamControlsDisabled(false);
                        countdownTimeline.play();
                    }
                    break;

                default:
                    break;
            }
        });
    }

    private void shutdown() {
        stopTimer();
        App.clearActiveExamCloseHandler();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
