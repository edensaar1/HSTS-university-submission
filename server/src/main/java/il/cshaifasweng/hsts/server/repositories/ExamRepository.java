package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.Exam;
import il.cshaifasweng.hsts.entities.enums.ExamStatus;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ExamRepository {

    public Exam getExamById(String examId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT e FROM Exam e " +
                                    "LEFT JOIN FETCH e.examQuestions " +
                                    "WHERE e.examId = :examId",
                            Exam.class
                    )
                    .setParameter("examId", examId)
                    .uniqueResult();
        }
    }



    public List<Exam> getPendingExamsForCoordinator(String coordinatorId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT e FROM Exam e " +
                                    "LEFT JOIN FETCH e.examQuestions eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE e.course.subjectCoordinator.userId = :coordinatorId " +
                                    "AND e.status = :status " +
                                    "ORDER BY e.examId",
                            Exam.class
                    )
                    .setParameter("coordinatorId", coordinatorId)
                    .setParameter("status", ExamStatus.PENDING_APPROVAL)
                    .getResultList();
        }
    }

    public List<Exam> getApprovedExamsForTeacher(String teacherId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT e FROM Exam e " +
                                    "LEFT JOIN FETCH e.examQuestions eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE e.course IN (" +
                                    "SELECT c FROM Teacher t JOIN t.courses c " +
                                    "WHERE t.userId = :teacherId) " +
                                    "AND e.status = :status " +
                                    "ORDER BY e.examId",
                            Exam.class
                    )
                    .setParameter("teacherId", teacherId)
                    .setParameter("status", ExamStatus.APPROVED)
                    .getResultList();
        }
    }





    public boolean saveExam(Exam exam){
        Transaction tx = null;

        try (Session session = DatabaseManager.getSession()) {
            tx = session.beginTransaction();

            session.save(exam);

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


    public boolean updateExam(Exam exam) {
        Transaction tx = null;

        try (Session session = DatabaseManager.getSession()) {
            tx = session.beginTransaction();
            session.merge(exam);
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
