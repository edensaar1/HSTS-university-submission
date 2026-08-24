package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.Course;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;
import java.util.List;

public class CourseRepository {


    public List<Course> getTeacherCourses(String teacherId){
        try(Session session = DatabaseManager.getSession()){
            return session.createQuery("SELECT c FROM Teacher t JOIN t.courses c  "
                    + "WHERE t.userId = :teacherId "
                    + "ORDER BY c.courseName", Course.class).setParameter("teacherId", teacherId).getResultList();
        }
    }

    public Course getCourseById(String courseId){
        try (Session session = DatabaseManager.getSession()) {
            return session.get(Course.class, courseId);
        }
    }


    public List<Course> getStudentCourses(String studentId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery("SELECT e.course FROM Enrollment e "
                    + "WHERE e.student.userId = :studentId "
                    + "ORDER BY e.course.courseName", Course.class)
                    .setParameter("studentId", studentId).getResultList();
        }
    }

    /*
    public List<Course> getSubjectCoordinatorCourses(String SubjectCoordinatorId){

    }
     */






}
