package il.cshaifasweng.hsts.entities;

import javax.persistence.*;

//Database-made entities don't have c'tor.//
@Entity
@Table(name = "courses")
public class Course {
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "coordinator_id", nullable = false)
    private SubjectCoordinator subjectCoordinator;

    @Id
    @Column(name = "course_id")
    private String courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    protected Course() {
    }

    public Subject getSubject() {
        return subject;
    }

    public SubjectCoordinator getSubjectCoordinator() {
        return subjectCoordinator;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }


}
