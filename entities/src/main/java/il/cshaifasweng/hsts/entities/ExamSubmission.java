package il.cshaifasweng.hsts.entities;


import il.cshaifasweng.hsts.entities.enums.ExamSubmissionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_submissions", uniqueConstraints = {@UniqueConstraint(columnNames = {"instance_id", "student_id"})})
public class ExamSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "instance_id", nullable = false)
    private ExamInstance examInstance;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamSubmissionStatus status = ExamSubmissionStatus.IN_PROGRESS;

    @Column(nullable = false)
    private boolean approved;

    @Column(nullable = false)
    private int actualDurationMinutes;

    private Integer automaticGrade;

    private Integer finalGrade;

    private String teacherComment;

    private String gradeChangeReason;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionAnswer> answers = new ArrayList<>();

    protected ExamSubmission() {
    }

    public ExamSubmission(ExamInstance examInstance, Student student, LocalDateTime startedAt) {
        if (examInstance == null) {
            throw new IllegalArgumentException("Exam instance cannot be null");
        }
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (startedAt == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        this.examInstance = examInstance;
        this.student = student;
        this.startedAt = startedAt;
        this.status = ExamSubmissionStatus.IN_PROGRESS;
        this.approved = false;
        this.actualDurationMinutes = 0;
    }




    public Long getSubmissionId() {
        return submissionId;
    }

    public ExamInstance getExamInstance() {
        return examInstance;
    }

    public Student getStudent() {
        return student;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public ExamSubmissionStatus getStatus() {
        return status;
    }

    public boolean isApproved() {
        return approved;
    }

    public int getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public List<SubmissionAnswer> getAnswers() {
        return answers;
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

    public void setExamInstance(ExamInstance examInstance) {
        if (examInstance == null) {
            throw new IllegalArgumentException("Exam instance cannot be null");
        }
        this.examInstance = examInstance;
    }

    public void setStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        this.student = student;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        if (startedAt == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (submittedAt != null && submittedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("Start time cannot be after submission time");
        }
        this.startedAt = startedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        if (submittedAt != null && submittedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("Submission time cannot be before start time");
        }
        this.submittedAt = submittedAt;
    }

    public void setStatus(ExamSubmissionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Submission status cannot be null");
        }
        this.status = status;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setActualDurationMinutes(int actualDurationMinutes) {
        if (actualDurationMinutes < 0) {
            throw new IllegalArgumentException("Actual duration cannot be negative");
        }
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public void setAutomaticGrade(Integer automaticGrade) {
        if(automaticGrade != null && (automaticGrade < 0 || automaticGrade > 100)){
            throw new IllegalArgumentException("Automatic grade must be between 0 and 100");
        }
        this.automaticGrade = automaticGrade;
    }

    public void setFinalGrade(Integer finalGrade) {
        if(finalGrade != null && (finalGrade < 0 || finalGrade > 100)){
            throw new IllegalArgumentException("Final grade must be between 0 and 100");
        }
        this.finalGrade = finalGrade;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public void setGradeChangeReason(String gradeChangeReason) {
        this.gradeChangeReason = gradeChangeReason;
    }

    public void addAnswer(SubmissionAnswer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Submission answer cannot be null");
        }
        answer.setSubmission(this);
        answers.add(answer);
    }

    public void removeAnswer(SubmissionAnswer answer) {
        if (answer == null) {
            return;
        }
        answers.remove(answer);
        answer.setSubmission(null);
    }
}
