package il.cshaifasweng.hsts.entities;

public class CurrentSession {
    private AuthorizedUser currentUser;

    public CurrentSession(AuthorizedUser currentUser){
        this.currentUser = currentUser;
    }

    public AuthorizedUser getCurrentUser() {
        return currentUser;
    }

    public String getUserId() {
        return currentUser.getUserId();
    }

    public String getFullName() {
        return currentUser.getFullName();
    }

    public String getRole(){
        return currentUser.getRole();
    }

}
