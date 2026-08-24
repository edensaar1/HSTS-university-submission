package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ExamSubmissionView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long submissionId;
    private Long instanceId;
    private String examId;
    private String courseName;
    private String studentId;
    private String studentName;
    private LocalDateTime submittedAt;
    private int actualDurationMinutes;
    private String status;
    private Integer automaticGrade;
    private Integer finalGrade;
    private String teacherComment;
    private String gradeChangeReason;
    private List<SubmissionReviewAnswerView> answers;

    public ExamSubmissionView(Long submissionId, Long instanceId, String examId, String courseName, String studentId,
                              String studentName, LocalDateTime submittedAt, int actualDurationMinutes, String status,
                              Integer automaticGrade, Integer finalGrade, String teacherComment,
                              String gradeChangeReason, List<SubmissionReviewAnswerView> answers) {
        this.submissionId = submissionId;
        this.instanceId = instanceId;
        this.examId = examId;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submittedAt = submittedAt;
        this.actualDurationMinutes = actualDurationMinutes;
        this.status = status;
        this.automaticGrade = automaticGrade;
        this.finalGrade = finalGrade;
        this.teacherComment = teacherComment;
        this.gradeChangeReason = gradeChangeReason;
        this.answers = answers;
    }

    public Long getSubmissionId() {
        return submissionId;
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

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public int getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public Integer getAutomaticGrade() {
        return automaticGrade;
    }

    public Integer getFinalGrade() {
        return finalGrade;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public String getGradeChangeReason() {
        return gradeChangeReason;
    }

    public List<SubmissionReviewAnswerView> getAnswers() {
        return answers;
    }
}
