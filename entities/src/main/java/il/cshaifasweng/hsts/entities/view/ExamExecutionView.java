package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ExamExecutionView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private Long submissionId;
    private String examId;
    private String courseName;
    private String studentInstructions;
    private int durationMinutes;
    private LocalDateTime startedAt;
    private List<StudentQuestionView> questions;

    public ExamExecutionView(Long instanceId, Long submissionId, String examId, String courseName, String studentInstructions,
                             int durationMinutes, LocalDateTime startedAt, List<StudentQuestionView> questions) {
        this.instanceId = instanceId;
        this.submissionId = submissionId;
        this.examId = examId;
        this.courseName = courseName;
        this.studentInstructions = studentInstructions;
        this.durationMinutes = durationMinutes;
        this.startedAt = startedAt;
        this.questions = questions;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public String getExamId() {
        return examId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStudentInstructions() {
        return studentInstructions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public List<StudentQuestionView> getQuestions() {
        return questions;
    }
}
