package il.cshaifasweng.hsts.client;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.client.state.ClientSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    private static Stage primaryStage;
    private static Runnable activeExamCloseHandler;


    @Override
    public void start(Stage stage) throws IOException{
        primaryStage = stage;
        SimpleClient client = SimpleClient.getClient();
        client.openConnection();
        primaryStage.setTitle("HSTS");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.setOnCloseRequest(event -> {
            if(ClientSession.getActiveExam() != null && activeExamCloseHandler != null){
                event.consume();
                activeExamCloseHandler.run();
            }
        });
        showLogin();
        primaryStage.show();

    }

    public static void showLogin() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/login.fxml");
    }

    public static void showMainMenu() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/main.fxml");
    }

    public static void showQuestionBank() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/question-bank.fxml");
    }

    public static void showExamCreation() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-creation.fxml");
    }

    public static void showExamApproval() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-approval.fxml");
    }

    public static void showExamManagement() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-management.fxml");
    }

    public static void showExamEntry() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-entry.fxml");
    }

    public static void showExamExecution() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-execution.fxml");
    }

    public static void showSubmissionManagement() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/exam-submission-management.fxml");
    }

    public static void showStudentResults() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/student-results.fxml");
    }

    public static void showTeacherStatistics() throws IOException{
        showScreen("/il/cshaifasweng/hsts/client/fxml/teacher-statistics.fxml");
    }

    public static void setActiveExamCloseHandler(Runnable closeHandler){
        activeExamCloseHandler = closeHandler;
    }

    public static void clearActiveExamCloseHandler(){
        activeExamCloseHandler = null;
    }

    public static void closeAfterExamSubmission(){
        activeExamCloseHandler = null;
        primaryStage.close();
    }

    private static void showScreen(String fxmlPath) throws IOException{
        URL resource = App.class.getResource(fxmlPath);
        if(resource == null){
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Scene scene = primaryStage.getScene();
        if(scene == null){
            primaryStage.setScene(new Scene(root));
        }
        else{
            scene.setRoot(root);
        }
    }


    @Override
    public void stop() throws IOException{
        SimpleClient client = SimpleClient.getClient();
        if (client.isConnected()) {
            client.closeConnection();
        }
        ClientSession.clear();

    }






}
