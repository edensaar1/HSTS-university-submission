package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class StudentResultView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private String examId;
    private String courseName;
    private LocalDateTime submittedAt;
    private int actualDurationMinutes;
    private String completionStatus;
    private int finalGrade;
    private String teacherComment;
    private List<SubmissionReviewAnswerView> answers;

    public StudentResultView(Long submissionId, String examId, String courseName, LocalDateTime submittedAt,
                             int actualDurationMinutes, String completionStatus, int finalGrade,
                             String teacherComment, List<SubmissionReviewAnswerView> answers) {
        this.submissionId = submissionId;
        this.examId = examId;
        this.courseName = courseName;
        this.submittedAt = submittedAt;
        this.actualDurationMinutes = actualDurationMinutes;
        this.completionStatus = completionStatus;
        this.finalGrade = finalGrade;
        this.teacherComment = teacherComment;
        this.answers = answers;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public int getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public int getFinalGrade() {
        return finalGrade;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public List<SubmissionReviewAnswerView> getAnswers() {
        return answers;
    }
}
