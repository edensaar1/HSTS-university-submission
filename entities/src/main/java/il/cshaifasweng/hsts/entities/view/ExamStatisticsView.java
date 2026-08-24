package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ExamStatisticsView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String examId;
    private String courseName;
    private String administeringTeacherName;
    private LocalDateTime openingTime;
    private int startedCount;
    private int submittedCount;
    private int timedOutCount;
    private int approvedGradesCount;
    private double averageGrade;
    private double medianGrade;
    private List<Integer> gradeDistribution;

    public ExamStatisticsView(Long instanceId, String examId, String courseName, String administeringTeacherName,
                              LocalDateTime openingTime, int startedCount, int submittedCount, int timedOutCount,
                              int approvedGradesCount, double averageGrade, double medianGrade,
                              List<Integer> gradeDistribution) {
        this.instanceId = instanceId;
        this.examId = examId;
        this.courseName = courseName;
        this.administeringTeacherName = administeringTeacherName;
        this.openingTime = openingTime;
        this.startedCount = startedCount;
        this.submittedCount = submittedCount;
        this.timedOutCount = timedOutCount;
        this.approvedGradesCount = approvedGradesCount;
        this.averageGrade = averageGrade;
        this.medianGrade = medianGrade;
        this.gradeDistribution = gradeDistribution;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public String getExamId() {
        return examId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getAdministeringTeacherName() {
        return administeringTeacherName;
    }

    public LocalDateTime getOpeningTime() {
        return openingTime;
    }

    public int getStartedCount() {
        return startedCount;
    }

    public int getSubmittedCount() {
        return submittedCount;
    }

    public int getTimedOutCount() {
        return timedOutCount;
    }

    public int getApprovedGradesCount() {
        return approvedGradesCount;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public double getMedianGrade() {
        return medianGrade;
    }

    public List<Integer> getGradeDistribution() {
        return gradeDistribution;
    }
}
