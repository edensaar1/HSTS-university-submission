package il.cshaifasweng.hsts.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//Database-made entities don't have c'tor.//
@Entity
@Table(name = "Teachers")
public class Teacher extends AuthorizedUser {
    @ManyToMany
    @JoinTable(name = "teacher_courses",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    protected Teacher() {
    }

    public List<Course> getCourses() {
        return courses;
    }
}
