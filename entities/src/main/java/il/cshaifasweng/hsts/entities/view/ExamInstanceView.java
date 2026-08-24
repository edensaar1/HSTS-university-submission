package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamInstanceView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String examId;
    private String courseId;
    private String courseName;
    private String administeringTeacherId;
    private String administeringTeacherName;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private String executionCode;
    private int originalDuration;
    private int extraTimeMinutes;

    public ExamInstanceView(
            String examId,
            LocalDateTime openingTime,
            LocalDateTime closingTime,
            String executionCode) {
        this.examId = examId;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.executionCode = executionCode;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getAdministeringTeacherId() {
        return administeringTeacherId;
    }

    public void setAdministeringTeacherId(String administeringTeacherId) {
        this.administeringTeacherId = administeringTeacherId;
    }

    public String getAdministeringTeacherName() {
        return administeringTeacherName;
    }

    public void setAdministeringTeacherName(String administeringTeacherName) {
        this.administeringTeacherName = administeringTeacherName;
    }

    public LocalDateTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalDateTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public void setExecutionCode(String executionCode) {
        this.executionCode = executionCode;
    }

    public int getOriginalDuration() {
        return originalDuration;
    }

    public void setOriginalDuration(int originalDuration) {
        this.originalDuration = originalDuration;
    }

    public int getExtraTimeMinutes() {
        return extraTimeMinutes;
    }

    public void setExtraTimeMinutes(int extraTimeMinutes) {
        this.extraTimeMinutes = extraTimeMinutes;
    }

    public int getEffectiveDuration() {
        return originalDuration + extraTimeMinutes;
    }
}
