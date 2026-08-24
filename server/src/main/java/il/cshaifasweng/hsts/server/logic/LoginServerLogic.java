package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.AuthorizedUser;
import il.cshaifasweng.hsts.entities.CurrentSession;
import il.cshaifasweng.hsts.server.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

public class LoginServerLogic {

    private final UserRepository userRepository;
    private final Set<String> loggedInUserIds;

    public LoginServerLogic(UserRepository userRepository){
        this.userRepository = userRepository;
        this.loggedInUserIds = new HashSet<>();
    }

    public synchronized CurrentSession login(String username, String password){
        if(username == null || username.isBlank()){
            throw new IllegalStateException("Username is required");
        }
        if(password == null || password.isBlank()){
            throw new IllegalStateException("Password is required");
        }

        AuthorizedUser user = userRepository.getUserByUsername(username);
        if(user == null){
            throw new IllegalStateException("Invalid username or password");
        }
        if(!user.getPassword().equals(password)){
            throw new IllegalStateException("Invalid username or password");
        }
        if (loggedInUserIds.contains(user.getUserId())) {
            throw new IllegalStateException("User is already logged in");
        }

        loggedInUserIds.add(user.getUserId());
        CurrentSession currentSession = new CurrentSession(user);
        return currentSession;
    }

    public synchronized void logout(CurrentSession currentSession){
        if(currentSession == null){
            return;
        }
        loggedInUserIds.remove(currentSession.getUserId());
    }

}
