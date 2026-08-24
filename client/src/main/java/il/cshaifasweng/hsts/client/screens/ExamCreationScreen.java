package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.logic.ExamCreationClientLogic;
import il.cshaifasweng.hsts.client.state.ClientSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.ExamStatus;
import il.cshaifasweng.hsts.entities.view.CourseView;
import il.cshaifasweng.hsts.entities.view.ExamQuestionView;
import il.cshaifasweng.hsts.entities.view.ExamView;
import il.cshaifasweng.hsts.entities.view.TeacherQuestionView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javafx.application.Platform;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import il.cshaifasweng.hsts.entities.enums.MessageType;

import il.cshaifasweng.hsts.client.App;
import java.io.IOException;

public class ExamCreationScreen {
    private ExamCreationClientLogic examCreationClientLogic;

    @FXML
    private ComboBox<CourseView> courseComboBox;
    @FXML
    private TableView<TeacherQuestionView> availableQuestionsTable;
    @FXML
    private TableColumn<TeacherQuestionView, String> availableQuestionIdColumn;
    @FXML
    private TableColumn<TeacherQuestionView, String> availableDescriptionColumn;
    @FXML
    private TextField pointsField;
    @FXML
    private TableView<ExamQuestionView> selectedQuestionsTable;
    @FXML
    private TableColumn<ExamQuestionView, String> selectedQuestionIdColumn;
    @FXML
    private TableColumn<ExamQuestionView, Integer> selectedPointsColumn;
    @FXML
    private TextField examIdField;
    @FXML
    private TextField durationField;
    @FXML
    private TextArea studentInstructionsArea;
    @FXML
    private TextArea teacherInstructionsArea;
    @FXML
    private Label totalPointsLabel;
    @FXML
    private Button createExamButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        examCreationClientLogic = new ExamCreationClientLogic();
        EventBus.getDefault().register(this);

        availableQuestionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        availableDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        selectedQuestionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        selectedPointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        examCreationClientLogic.requestTeacherCourses();

    }

    @Subscribe
    public void onHSTSMessage(HSTSMessage hstsMessage) {
        // Response handling is implemented in a later batch.

        Platform.runLater(() -> {
            MessageType type = hstsMessage.getType();
            switch(type) {
                case GET_TEACHER_COURSES_RESPONSE:
                    List<CourseView> teacherCourses = (List<CourseView>) hstsMessage.getPayload();
                    courseComboBox.getItems().setAll(teacherCourses);
                    showStatus("");
                    break;
                case GET_COURSE_QUESTIONS_RESPONSE:
                    List<TeacherQuestionView> courseQuestions = (List<TeacherQuestionView>) hstsMessage.getPayload();
                    availableQuestionsTable.getItems().setAll(courseQuestions);
                    showStatus("");
                    break;

                case CREATE_EXAM_RESPONSE:
                    boolean createdExam = (boolean) hstsMessage.getPayload();
                    createExamButton.setDisable(false);
                    if(createdExam){
                        showStatus("Exam created successfully");
                        clearExamFields();
                    }
                    else{
                        showStatus("Could not create exam");
                    }
                    break;



                case ERROR:
                    showStatus((String) hstsMessage.getPayload());
                    createExamButton.setDisable(false);
                    break;
            }

        });




    }

    @FXML
    public void handleAddQuestion() {
        TeacherQuestionView teacherQuestionView = availableQuestionsTable.getSelectionModel().getSelectedItem();
        if(teacherQuestionView == null){
            showStatus("Please select a question");
            return;
        }
        if(pointsField.getText().isBlank()){
            showStatus("Please fill question's points");
            return;
        }
        int points;
        try {
            points = Integer.parseInt(pointsField.getText().trim());
        } catch (NumberFormatException e) {
            showStatus("Points must be a whole number");
            return;
        }

        if(points <= 0){
            showStatus("Points must be positive");
            return;
        }

        for(ExamQuestionView selected : selectedQuestionsTable.getItems()){
            if(selected.getQuestionId().equals(teacherQuestionView.getQuestionId())){
                showStatus("Question is already selected");
                return;
            }
        }

        int currentTotal = 0;
        for(ExamQuestionView selected : selectedQuestionsTable.getItems()){
            currentTotal += selected.getPoints();
        }
        if(currentTotal + points > 100){
            showStatus("Total points cannot exceed 100");
            return;
        }

        ExamQuestionView examQuestionView = new ExamQuestionView(
                teacherQuestionView.getQuestionId(), points);
        selectedQuestionsTable.getItems().add(examQuestionView);
        pointsField.clear();
        totalPointsLabel.setText((currentTotal + points) + " / 100");
        showStatus("");



    }



    @FXML
    public void handleRemoveQuestion() {
        ExamQuestionView selectedQuestion = selectedQuestionsTable.getSelectionModel().getSelectedItem();
        if(selectedQuestion == null){
            showStatus("Please select a question to remove");
            return;
        }
        selectedQuestionsTable.getItems().remove(selectedQuestion);
        int currentTotal = 0;
        for(ExamQuestionView selected : selectedQuestionsTable.getItems()){
            currentTotal += selected.getPoints();

        }
        totalPointsLabel.setText(currentTotal + " / 100");
        showStatus("");
    }

    @FXML
    public void handleCreateExam() {
        CourseView selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        if(selectedCourse == null){
            showStatus("Please select a course");
            return;
        }

        if(examIdField.getText() == null
                || !examIdField.getText().trim().matches("\\d{6}")){
            showStatus("Exam ID must contain exactly six digits");
            return;
        }

        if(durationField.getText().isBlank()){
            showStatus("Please fill duration");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationField.getText().trim());
        }
        catch (NumberFormatException e) {
            showStatus("Duration must be a whole number");
            return;
        }

        if(duration <= 0){
            showStatus("Duration must positive");
            return;
        }

        List<ExamQuestionView> examQuestionViewList  = selectedQuestionsTable.getItems();
        if(examQuestionViewList.isEmpty()){
            showStatus("Exam must contain at least one question");
            return;
        }

        int totalPoints = 0;
        for(ExamQuestionView question : examQuestionViewList){
            totalPoints += question.getPoints();
        }
        if(totalPoints != 100){
            showStatus("Total points should be exactly 100");
            return;
        }

        ExamView examView = new ExamView(selectedCourse.getCourseId(), selectedCourse.getCourseName(),
                ClientSession.getCurrentSession().getUserId(), ClientSession.getCurrentSession().getFullName(),
                examIdField.getText().trim(), duration,
                studentInstructionsArea.getText(), teacherInstructionsArea.getText());


        examView.setExamQuestions(new ArrayList<>(examQuestionViewList));
        examView.setStatus(ExamStatus.DRAFT);

        createExamButton.setDisable(true);
        showStatus("Creating exam...");
        examCreationClientLogic.requestCreateExam(examView);






    }

    @FXML
    public void handleBack() {
        try{
            App.showMainMenu();
            shutdown();
        } catch (IOException e) {
            showStatus("Could not return to main menu");
        }
    }

    @FXML
    public void handleLoadQuestions() {
        CourseView selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        if(selectedCourse == null){
            showStatus("Please select a course");
            return;
        }
        availableQuestionsTable.getItems().clear();
        selectedQuestionsTable.getItems().clear();
        pointsField.clear();
        totalPointsLabel.setText("0 / 100");

        examCreationClientLogic.requestCourseQuestions(selectedCourse.getCourseId());
        showStatus("Loading questions...");
    }



    private void shutdown(){
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private void clearExamFields(){
        selectedQuestionsTable.getItems().clear();
        pointsField.clear();
        totalPointsLabel.setText("0 / 100");

        examIdField.clear();
        durationField.clear();
        studentInstructionsArea.clear();
        teacherInstructionsArea.clear();
    }

    private void showStatus(String message){
        statusLabel.setText(message);
    }
}
