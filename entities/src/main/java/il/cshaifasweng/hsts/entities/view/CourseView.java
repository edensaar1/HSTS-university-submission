package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class CourseView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseId;
    private String courseName;


    public CourseView(String courseId, String courseName){
        this.courseId = courseId;
        this.courseName = courseName;
    }




    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public String toString() {
        return courseId + " - " + courseName;
    }




    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

}
