package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class AuthorizedUserView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String role;

    public AuthorizedUserView(String userId, String fullName, String role){
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
