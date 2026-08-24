package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.CurrentSession;
import il.cshaifasweng.hsts.entities.ExamInstance;
import il.cshaifasweng.hsts.entities.ExamSubmission;
import il.cshaifasweng.hsts.entities.Teacher;
import il.cshaifasweng.hsts.entities.enums.ExamSubmissionStatus;
import il.cshaifasweng.hsts.entities.view.ExamStatisticsView;
import il.cshaifasweng.hsts.server.repositories.StatisticsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherStatisticsServerLogic {
    private final StatisticsRepository statisticsRepository;

    public TeacherStatisticsServerLogic(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public List<ExamStatisticsView> getTeacherStatistics(CurrentSession currentSession) {
        if (currentSession == null) {
            throw new IllegalArgumentException("Current session cannot be null");
        }
        if (!"Teacher".equals(currentSession.getRole()) || !(currentSession.getCurrentUser() instanceof Teacher)) {
            throw new IllegalStateException("Only teachers can view exam statistics");
        }
        List<ExamInstance> examInstances = statisticsRepository.
                getCompletedInstancesForExamAuthor(currentSession.getUserId());
        List<ExamStatisticsView> statisticsViews = new ArrayList<>();

        for (ExamInstance instance : examInstances) {
            List<ExamSubmission> submissions = statisticsRepository.getSubmissionsForInstance(instance.getInstanceId());

            int startedCount = submissions.size();
            int submittedCount = 0;
            int timedOutCount = 0;
            List<Integer> approvedGrades = new ArrayList<>();

            for (ExamSubmission submission : submissions) {

                if (submission.getStatus() == ExamSubmissionStatus.SUBMITTED) {
                    submittedCount++;
                }

                if (submission.getStatus() == ExamSubmissionStatus.TIMED_OUT) {
                    timedOutCount++;
                }

                if (submission.isApproved() && submission.getFinalGrade() != null) {
                    approvedGrades.add(submission.getFinalGrade());
                }
            }

            List<Integer> gradeDistribution = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                gradeDistribution.add(0);
            }

            int totalGrades = 0;
            for(Integer grade : approvedGrades){
                totalGrades += grade;

                int bucket = grade == 100 ? 9 : grade / 10;

                gradeDistribution.set(bucket, gradeDistribution.get(bucket) + 1);
            }

            double averageGrade = 0.0;

            if(!approvedGrades.isEmpty()){
                averageGrade = (double) totalGrades / approvedGrades.size();
            }

            Collections.sort(approvedGrades);
            double medianGrade = 0.0;

            if(!approvedGrades.isEmpty()){
                int middle = approvedGrades.size() / 2;

                if(approvedGrades.size() % 2 == 1){
                    medianGrade = approvedGrades.get(middle);
                }
                else{
                    medianGrade = (approvedGrades.get(middle - 1) +
                            approvedGrades.get(middle)) / 2.0;
                }
            }

            ExamStatisticsView statisticsView = new ExamStatisticsView(
                    instance.getInstanceId(), instance.getExam().getExamId(),
                    instance.getExam().getCourse().getCourseName(),
                    instance.getAdministeringTeacher().getFullName(), instance.getOpeningTime(),
                    startedCount, submittedCount, timedOutCount, approvedGrades.size(),
                    averageGrade, medianGrade, gradeDistribution);

            statisticsViews.add(statisticsView);
        }

        return statisticsViews;
    }

}
