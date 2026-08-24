package il.cshaifasweng.hsts.entities.view;

import il.cshaifasweng.hsts.entities.enums.ExamStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String examId;

    // Author information
    private String teacherId;
    private String teacherName;

    // Course information
    private String courseId;
    private String courseName;

    private int duration;
    private String studentInstructions;
    private String teacherInstructions;

    private ExamStatus status;
    private String rejectionReason;

    private List<ExamQuestionView> examQuestions = new ArrayList<>();


    public ExamView(String courseId, String courseName, String teacherId, String teacherName , String examId,
                    int duration, String studentInstructions, String teacherInstructions){
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.examId = examId;
        this.duration = duration;
        this.studentInstructions = studentInstructions;
        this.teacherInstructions = teacherInstructions;

    }





    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public List<ExamQuestionView> getExamQuestions() {
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


    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setExamQuestions(List<ExamQuestionView> examQuestions) {
        this.examQuestions = examQuestions;
    }

    public void setExamId(String examId) {
        this.examId = examId;
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





























}
