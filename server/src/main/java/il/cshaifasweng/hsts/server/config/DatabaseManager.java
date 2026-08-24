package il.cshaifasweng.hsts.server.config;

import il.cshaifasweng.hsts.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class DatabaseManager {

    private static SessionFactory sessionFactory;

    public static void initialize(){
        if(sessionFactory == null){
            Configuration configuration = new Configuration().configure();
            applyEnvironmentOverride(configuration, "HSTS_DB_URL", "hibernate.connection.url");
            applyEnvironmentOverride(configuration, "HSTS_DB_USERNAME", "hibernate.connection.username");
            applyEnvironmentOverride(configuration, "HSTS_DB_PASSWORD", "hibernate.connection.password");

            sessionFactory = configuration
                    .addAnnotatedClass(AuthorizedUser.class)
                    .addAnnotatedClass(Teacher.class)
                    .addAnnotatedClass(Student.class)
                    .addAnnotatedClass(SubjectCoordinator.class)
                    .addAnnotatedClass(Subject.class)
                    .addAnnotatedClass(Course.class)
                    .addAnnotatedClass(Enrollment.class)
                    .addAnnotatedClass(Question.class)
                    .addAnnotatedClass(Exam.class)
                    .addAnnotatedClass(ExamQuestion.class)
                    .addAnnotatedClass(ExamInstance.class)
                    .addAnnotatedClass(ExamSubmission.class)
                    .addAnnotatedClass(SubmissionAnswer.class)
                    .buildSessionFactory();
        }
    }

    private static void applyEnvironmentOverride(Configuration configuration, String environmentName,
                                                 String hibernateProperty) {
        String value = System.getenv(environmentName);
        if(value != null && !value.isBlank()){
            configuration.setProperty(hibernateProperty, value);
        }
    }

    public static Session getSession(){
        if(sessionFactory == null){
            initialize();
        }
        return sessionFactory.openSession();
    }

    public static void close(){
        if(sessionFactory != null){
            sessionFactory.close();
            sessionFactory = null;
        }
    }

}
