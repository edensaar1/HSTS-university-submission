package il.cshaifasweng.hsts.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Subject_Coordinators")
public class SubjectCoordinator extends AuthorizedUser {
    protected SubjectCoordinator() {
    }
}
