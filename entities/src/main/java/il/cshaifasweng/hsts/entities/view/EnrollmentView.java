package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class EnrollmentView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private String courseId;
    private Integer grade;

    public EnrollmentView(String studentId, String courseId, Integer grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
