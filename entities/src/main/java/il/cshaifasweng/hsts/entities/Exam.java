package il.cshaifasweng.hsts.entities;

import il.cshaifasweng.hsts.entities.enums.ExamStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
public class Exam {
    //references//
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "exam",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> examQuestions = new ArrayList<>();

//field//
    @Id
    @Column(name = "exam_id", length = 6)
    private String examId;

    @Column(nullable = false)
    private int duration;

    @Column(name = "student_Instructions")
    private String studentInstructions;

    @Column(name = "teacher_Instructions")
    private String teacherInstructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.DRAFT;

    private String rejectionReason;


    protected Exam(){
    }

    public Exam(Course course, Teacher teacher, String examId, int duration, String studentInstructions,
                String teacherInstructions){
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        if (examId == null || !examId.matches("\\d{6}")) {
            throw new IllegalArgumentException("Exam ID must contain exactly 6 digits");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        this.course =course;
        this.teacher = teacher;
        this.examId = examId;
        this.duration = duration;
        this.studentInstructions = studentInstructions;
        this.teacherInstructions = teacherInstructions;

    }

    public Course getCourse() {
        return course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public List<ExamQuestion> getExamQuestions() {
        return examQuestions;
    }

    public String getExamId() {
        return examId;
    }

    public int getDuration() {
        return duration;
    }

    public String getStudentInstructions() {
        return studentInstructions;
    }

    public String getTeacherInstructions() {
        return teacherInstructions;
    }

    public ExamStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStudentInstructions(String studentInstructions) {
        this.studentInstructions = studentInstructions;
    }

    public void setTeacherInstructions(String teacherInstructions) {
        this.teacherInstructions = teacherInstructions;
    }

    public void setStatus(ExamStatus status) {
        this.status = status;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }







    public boolean isApproved(){
        return status == ExamStatus.APPROVED;
    }

    public boolean canBePublished(){
        return isApproved(); // maybe add more checks
    }

    public int calculateTotalPoints(){
        int totalPoints = 0;
        for(ExamQuestion examQuestion : examQuestions){
            totalPoints += examQuestion.getPoints();
        }
        return totalPoints;
    }

    public void addExamQuestion(ExamQuestion examQuestion) {
        if (examQuestion == null) {
            throw new IllegalArgumentException("Exam question cannot be null");
        }
        examQuestions.add(examQuestion);
        examQuestion.setExam(this);
    }

    public void removeExamQuestion(ExamQuestion examQuestion) {
        if(examQuestions.remove(examQuestion)) {
            examQuestion.setExam(null);
        }
    }


    public void submitForApproval(){
        if(status != ExamStatus.DRAFT){
            throw new IllegalStateException("Exam status should be DRAFT");
        }
        setStatus(ExamStatus.PENDING_APPROVAL);
        setRejectionReason(null);
    }
    public void approve(){
        if(status != ExamStatus.PENDING_APPROVAL){
            throw new IllegalStateException("Exam status should be PENDING_APPROVAL");
        }
        setStatus(ExamStatus.APPROVED);
        setRejectionReason(null);
    }

    public void reject(String reason){
        if(status != ExamStatus.PENDING_APPROVAL){
            throw new IllegalStateException("Exam status should be PENDING_APPROVAL");
        }
        if(reason == null || reason.isBlank()){
            throw new IllegalArgumentException("Rejection reason is required");
        }
        setStatus(ExamStatus.REJECTED);
        setRejectionReason(reason.trim());
    }


}
