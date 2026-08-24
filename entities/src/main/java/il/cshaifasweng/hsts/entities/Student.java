package il.cshaifasweng.hsts.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Students")
public class Student extends AuthorizedUser {
    protected Student() {
    }
}
