package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.AuthorizedUser;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;

public class UserRepository {

    public AuthorizedUser getUserByUsername(String username){
        try(Session session = DatabaseManager.getSession()){
            return session.createQuery("FROM AuthorizedUser u WHERE u.username =:username", AuthorizedUser.class)
                    .setParameter("username", username).uniqueResult();
        }
    }


}
