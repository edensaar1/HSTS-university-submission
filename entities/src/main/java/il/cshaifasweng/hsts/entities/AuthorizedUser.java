package il.cshaifasweng.hsts.entities;

import javax.persistence.*;

//Database-made entities don't have c'tor.//
@Entity
@Table(name = "Authorized_Users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AuthorizedUser {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String role;

    protected AuthorizedUser(){
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

}
