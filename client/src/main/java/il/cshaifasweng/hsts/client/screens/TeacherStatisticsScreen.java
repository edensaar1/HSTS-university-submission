package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.TeacherStatisticsClientLogic;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.view.ExamStatisticsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherStatisticsScreen {
    private TeacherStatisticsClientLogic teacherStatisticsClientLogic;

    @FXML private TableView<ExamStatisticsView> statisticsTable;
    @FXML private TableColumn<ExamStatisticsView, Number> instanceIdColumn;
    @FXML private TableColumn<ExamStatisticsView, String> examIdColumn;
    @FXML private TableColumn<ExamStatisticsView, String> courseColumn;
    @FXML private TableColumn<ExamStatisticsView, String> administeringTeacherColumn;
    @FXML private TableColumn<ExamStatisticsView, LocalDateTime> openingTimeColumn;

    @FXML private Label selectedInstanceLabel;
    @FXML private Label startedValueLabel;
    @FXML private Label submittedValueLabel;
    @FXML private Label timedOutValueLabel;
    @FXML private Label approvedGradesValueLabel;
    @FXML private Label averageValueLabel;
    @FXML private Label medianValueLabel;
    @FXML private BarChart<String, Number> distributionChart;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        teacherStatisticsClientLogic = new TeacherStatisticsClientLogic();
        EventBus.getDefault().register(this);

        instanceIdColumn.setCellValueFactory(new PropertyValueFactory<>("instanceId"));
        examIdColumn.setCellValueFactory(new PropertyValueFactory<>("examId"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        administeringTeacherColumn.setCellValueFactory(
                new PropertyValueFactory<>("administeringTeacherName"));
        openingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("openingTime"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        openingTimeColumn.setCellFactory(column -> new TableCell<ExamStatisticsView, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime openingTime, boolean empty) {
                super.updateItem(openingTime, empty);
                setText(empty || openingTime == null ? "" : formatter.format(openingTime));
            }
        });

        statisticsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldStatistics, selectedStatistics) -> showStatistics(selectedStatistics));
        requestStatistics();
    }

    private void showStatistics(ExamStatisticsView statistics) {
        distributionChart.getData().clear();

        if(statistics == null){
            selectedInstanceLabel.setText("Select an exam instance");
            startedValueLabel.setText("-");
            submittedValueLabel.setText("-");
            timedOutValueLabel.setText("-");
            approvedGradesValueLabel.setText("-");
            averageValueLabel.setText("-");
            medianValueLabel.setText("-");
            return;
        }

        selectedInstanceLabel.setText(
                "Exam " + statistics.getExamId() + " - Instance " + statistics.getInstanceId());
        startedValueLabel.setText(String.valueOf(statistics.getStartedCount()));
        submittedValueLabel.setText(String.valueOf(statistics.getSubmittedCount()));
        timedOutValueLabel.setText(String.valueOf(statistics.getTimedOutCount()));
        approvedGradesValueLabel.setText(String.valueOf(statistics.getApprovedGradesCount()));
        averageValueLabel.setText(String.format("%.2f", statistics.getAverageGrade()));
        medianValueLabel.setText(String.format("%.2f", statistics.getMedianGrade()));

        String[] ranges = {"0-9", "10-19", "20-29", "30-39", "40-49",
                "50-59", "60-69", "70-79", "80-89", "90-100"};
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Students");

        List<Integer> distribution = statistics.getGradeDistribution();
        for(int i = 0; i < ranges.length; i++){
            series.getData().add(new XYChart.Data<>(ranges[i], distribution.get(i)));
        }
        distributionChart.getData().add(series);
    }

    @FXML
    private void handleRefresh() {
        statisticsTable.getSelectionModel().clearSelection();
        requestStatistics();
    }

    private void requestStatistics() {
        statusLabel.setText("Loading statistics...");
        try {
            teacherStatisticsClientLogic.requestTeacherStatistics();
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
                case GET_TEACHER_STATISTICS_RESPONSE:
                    List<ExamStatisticsView> statistics =
                            (List<ExamStatisticsView>) message.getPayload();
                    statisticsTable.getItems().setAll(statistics);
                    statisticsTable.getSelectionModel().clearSelection();
                    if(!statistics.isEmpty()){
                        statisticsTable.getSelectionModel().selectFirst();
                    }
                    statusLabel.setText(statistics.size() + " completed exam instance(s) loaded");
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
