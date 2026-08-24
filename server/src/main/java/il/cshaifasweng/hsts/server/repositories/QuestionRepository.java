package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.Question;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class QuestionRepository {

    public List<Question> getCourseQuestions(String courseId){
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery("FROM Question q WHERE q.course.courseId = :courseId ORDER BY q.questionId", Question.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    public Question getQuestionById(String questionId){
        try (Session session = DatabaseManager.getSession()) {
            return session.get(Question.class, questionId);
        }
    }

    public boolean createQuestion(Question question){
        Transaction tx = null;

        try (Session session = DatabaseManager.getSession()) {
            tx = session.beginTransaction();

            session.save(question);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQuestion(Question question){
        Transaction tx = null;

        try (Session session = DatabaseManager.getSession()) {
            tx = session.beginTransaction();

            session.update(question);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuestion(Question question){
        Transaction tx = null;

        try (Session session = DatabaseManager.getSession()) {
            tx = session.beginTransaction();

            if (question == null) {
                tx.rollback();
                return false;
            }

            session.delete(question);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

}
