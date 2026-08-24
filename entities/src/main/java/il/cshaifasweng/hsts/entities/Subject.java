package il.cshaifasweng.hsts.entities;

import javax.persistence.*;


//Database-made entities don't have c'tor.//
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @Column(name = "subject_id")
    private String subjectId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    protected Subject() {
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
