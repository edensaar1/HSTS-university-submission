package il.cshaifasweng.hsts.entities.view;

public class TeacherView extends AuthorizedUserView {
    private static final long serialVersionUID = 1L;

    public TeacherView(String userId, String fullName, String role) {
        super(userId, fullName, role);
    }
}
