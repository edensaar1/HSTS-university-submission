package il.cshaifasweng.hsts.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Locale;


@Entity
@Table(name = "exam_instances")
public class ExamInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long instanceId;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "administering_teacher_id", nullable = false)
    private Teacher administeringTeacher;

    @Column(nullable = false)
    private LocalDateTime openingTime;

    @Column(nullable = false)
    private LocalDateTime closingTime;

    @Column(nullable = false, length = 4)
    private String executionCode;

    @Column(nullable = false)
    private int extraTimeMinutes = 0;

    protected ExamInstance() {
    }

    public ExamInstance(Exam exam, Teacher administeringTeacher, LocalDateTime openingTime, LocalDateTime closingTime,
            String executionCode){
        if (exam == null) {
            throw new IllegalArgumentException("Exam cannot be null");
        }
        if (!exam.isApproved()) {
            throw new IllegalStateException("Only an approved exam can be scheduled");
        }
        if (administeringTeacher == null) {
            throw new IllegalArgumentException("Administering teacher cannot be null");
        }
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times are required");
        }
        if (!closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
        if (executionCode == null || executionCode.isBlank()) {
            throw new IllegalArgumentException("Execution code is required");
        }

        String normalizedCode = executionCode.trim().toUpperCase(Locale.ROOT);
        if (!normalizedCode.matches("[A-Z0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Execution code must contain exactly four letters or digits");
        }

        this.exam = exam;
        this.administeringTeacher = administeringTeacher;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.executionCode = normalizedCode;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        if (exam == null) {
            throw new IllegalArgumentException("Exam cannot be null");
        }
        if (!exam.isApproved()) {
            throw new IllegalStateException("Only an approved exam can be scheduled");
        }
        this.exam = exam;
    }

    public Teacher getAdministeringTeacher() {
        return administeringTeacher;
    }

    public void setAdministeringTeacher(Teacher administeringTeacher) {
        if (administeringTeacher == null) {
            throw new IllegalArgumentException("Administering teacher cannot be null");
        }
        this.administeringTeacher = administeringTeacher;
    }

    public LocalDateTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalDateTime openingTime) {
        if (openingTime == null) {
            throw new IllegalArgumentException("Opening time is required");
        }
        if (closingTime != null && !closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException("Opening time must be before closing time");
        }
        this.openingTime = openingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        if (closingTime == null) {
            throw new IllegalArgumentException("Closing time is required");
        }
        if (openingTime != null && !closingTime.isAfter(openingTime)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
        this.closingTime = closingTime;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public void setExecutionCode(String executionCode) {
        if (executionCode == null || executionCode.isBlank()) {
            throw new IllegalArgumentException("Execution code is required");
        }
        String normalizedCode = executionCode.trim().toUpperCase(Locale.ROOT);
        if (!normalizedCode.matches("[A-Z0-9]{4}")) {
            throw new IllegalArgumentException(
                    "Execution code must contain exactly four letters or digits");
        }
        this.executionCode = normalizedCode;
    }

    public int getExtraTimeMinutes() {
        return extraTimeMinutes;
    }

    public void setExtraTimeMinutes(int extraTimeMinutes) {
        if (extraTimeMinutes < 0) {
            throw new IllegalArgumentException("Extra time cannot be negative");
        }
        this.extraTimeMinutes = extraTimeMinutes;
    }



    public int getEffectiveDuration(){
        return exam.getDuration() + extraTimeMinutes;
    }

    public void extendDuration(int additionalMinutes){
        if(additionalMinutes <= 0){
            throw new IllegalArgumentException("Additional minutes must be positive");
        }
        extraTimeMinutes += additionalMinutes;
    }






}
