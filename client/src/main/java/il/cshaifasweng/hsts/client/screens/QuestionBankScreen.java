package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.logic.QuestionBankClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.Question;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.view.CourseView;
import il.cshaifasweng.hsts.entities.view.TeacherQuestionView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.hsts.client.App;
import java.io.IOException;

import java.io.File;
import java.util.List;

public class QuestionBankScreen {
    private QuestionBankClientLogic questionBankClientLogic;

    @FXML
    private ComboBox<CourseView> courseComboBox;

    @FXML
    private TableView<TeacherQuestionView> questionsTable;

    @FXML
    private TableColumn<TeacherQuestionView, String> questionIdColumn;

    @FXML
    private TableColumn<TeacherQuestionView, String> descriptionColumn;

    @FXML
    private TableColumn<TeacherQuestionView, Integer> correctAnswerColumn;

    @FXML
    private TextField questionIdField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField answer1Field;

    @FXML
    private TextField answer2Field;

    @FXML
    private TextField answer3Field;

    @FXML
    private TextField answer4Field;

    @FXML
    private ComboBox<Integer> correctAnswerComboBox;

    @FXML
    private TextField illustrationPathField;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label statusLabel;


    @FXML
    private void initialize() {
        questionBankClientLogic = new QuestionBankClientLogic();
        EventBus.getDefault().register(this);

        correctAnswerComboBox.getItems().addAll(1, 2, 3, 4);

        questionIdColumn.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        questionsTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable,
                              oldQuestion, selectedQuestion) -> {
                    if (selectedQuestion != null) {
                        fillQuestionFields(selectedQuestion);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                });

        questionBankClientLogic.requestTeacherCourses();

    }

    @Subscribe
    public void onHSTSMessage(HSTSMessage hstsMessage) {
        Platform.runLater(() -> {
            MessageType type = hstsMessage.getType();
            switch (type) {
                case GET_TEACHER_COURSES_RESPONSE:
                    List<CourseView> teacherCourses = (List<CourseView>) hstsMessage.getPayload();
                    courseComboBox.getItems().setAll(teacherCourses);
                    break;

                case GET_COURSE_QUESTIONS_RESPONSE:
                    List<TeacherQuestionView> teacherQuestions = (List<TeacherQuestionView>) hstsMessage.getPayload();
                    questionsTable.getItems().setAll(teacherQuestions);
                    clearQuestionFields();
                    break;

                case CREATE_QUESTION_RESPONSE:
                    boolean status1 = (boolean) hstsMessage.getPayload();
                    if (status1) {
                        clearQuestionFields();
                        showStatus("Question created successfully");
                        CourseView courseAfterCreate = courseComboBox.getValue();
                        if (courseAfterCreate != null) {
                            questionBankClientLogic.requestCourseQuestions(courseAfterCreate.getCourseId());
                        }
                    }
                    break;

                case UPDATE_QUESTION_RESPONSE:
                    boolean status2 = (boolean) hstsMessage.getPayload();
                    if (status2) {
                        clearQuestionFields();
                        showStatus("Question updated successfully");
                        CourseView courseAfterUpdate = courseComboBox.getValue();
                        if (courseAfterUpdate != null) {
                            questionBankClientLogic.requestCourseQuestions(courseAfterUpdate.getCourseId());
                        }
                    }
                    break;

                case DELETE_QUESTION_RESPONSE:
                    boolean status3 = (boolean) hstsMessage.getPayload();
                    if (status3) {
                        clearQuestionFields();
                        showStatus("Question deleted successfully");
                        CourseView courseAfterDelete = courseComboBox.getValue();
                        if (courseAfterDelete != null) {
                            questionBankClientLogic.requestCourseQuestions(courseAfterDelete.getCourseId());
                        }
                    }
                    break;

                case ERROR:
                    statusLabel.setText((String) hstsMessage.getPayload());
                    break;
            }
        });
    }


    @FXML
    private void handleLoadQuestions(){
        CourseView selectedCourse = courseComboBox.getValue();
        if(selectedCourse == null){
            showStatus("Please select a course");
            return;
        }
        clearQuestionFields();
        showStatus("");
        questionBankClientLogic.requestCourseQuestions(selectedCourse.getCourseId());
    }

    @FXML
    private void handleCreate(){
        if(!areRequiredFieldsFilled()){
            return;
        }
        TeacherQuestionView question = buildQuestionViewFromFields();
        questionBankClientLogic.requestCreateQuestion(question);
        showStatus("Creating question...");

    }


    @FXML
    private void handleUpdate(){
        TeacherQuestionView question = questionsTable.getSelectionModel().getSelectedItem();
        if(question == null){
            showStatus("Please select a question to update");
            return;
        }
        if(!areRequiredFieldsFilled()){
            return;
        }
        question = buildQuestionViewFromFields();
        questionBankClientLogic.requestUpdateQuestion(question);
        showStatus("Updating Question...");
    }


    @FXML
    private void handleDelete(){
        TeacherQuestionView question = questionsTable.getSelectionModel().getSelectedItem();
        if(question == null){
            showStatus("Please select a question to delete");
            return;
        }
        questionBankClientLogic.requestDeleteQuestion(question.getQuestionId());
        showStatus("Deleting Question...");
    }

    @FXML
    private void handleChooseIllustration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Question Illustration");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File selectedFile = fileChooser.showOpenDialog(illustrationPathField.getScene().getWindow());

        if (selectedFile != null) {
            illustrationPathField.setText(selectedFile.getAbsolutePath());
        }
    }












    private boolean areRequiredFieldsFilled(){
        if(courseComboBox.getValue() == null){
            showStatus("Please select a course");
            return false;
        }
        else if(questionIdField.getText().isBlank()) {
            showStatus("Please enter a question ID");
            return false;
        }
        else if(descriptionArea.getText().isBlank()) {
            showStatus("Please enter a description");
            return false;
        }
        else if(answer1Field.getText().isBlank() || answer2Field.getText().isBlank()
                || answer3Field.getText().isBlank() || answer4Field.getText().isBlank()) {
            showStatus("Please enter all four answers");
            return false;
        }
        else if(correctAnswerComboBox.getValue() == null) {
            showStatus("Please select the correct answer");
            return false;
        }
        else{
            showStatus("");
            return true;
        }
    }


    private void fillQuestionFields(TeacherQuestionView question){
        questionIdField.setText(question.getQuestionId());
        descriptionArea.setText(question.getDescription());
        answer1Field.setText(question.getAnswer1());
        answer2Field.setText(question.getAnswer2());
        answer3Field.setText(question.getAnswer3());
        answer4Field.setText(question.getAnswer4());
        correctAnswerComboBox.setValue(question.getCorrectAnswer());
        illustrationPathField.setText(question.getIllustrationPath());

        questionIdField.setDisable(true);
    }

    private void clearQuestionFields(){
        questionsTable.getSelectionModel().clearSelection();
        questionIdField.clear();
        descriptionArea.clear();
        answer1Field.clear();
        answer2Field.clear();
        answer3Field.clear();
        answer4Field.clear();
        correctAnswerComboBox.getSelectionModel().clearSelection();
        illustrationPathField.clear();

        questionIdField.setDisable(false);

        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        showStatus("");

    }

    private TeacherQuestionView buildQuestionViewFromFields(){
        CourseView selectedCourse = courseComboBox.getValue();

        TeacherQuestionView buildQuestion = new TeacherQuestionView(selectedCourse.getCourseId(),
                questionIdField.getText(), descriptionArea.getText(), answer1Field.getText(), answer2Field.getText(),
                answer3Field.getText(), answer4Field.getText(), correctAnswerComboBox.getValue(),
                illustrationPathField.getText());
        return buildQuestion;
    }

    private void showStatus(String message){
        statusLabel.setText(message);
    }


    public void handleBack() {
        try {
            App.showMainMenu();
            shutdown();
        } catch (IOException e) {
            showStatus("Could not return to main menu");
        }
    }

    private void shutdown() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
