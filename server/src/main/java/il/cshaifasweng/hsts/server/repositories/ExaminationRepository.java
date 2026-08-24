package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.ExamInstance;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class ExaminationRepository {

    public boolean saveExamInstance(ExamInstance examInstance) {
        Transaction transaction = null;

        try (Session session = DatabaseManager.getSession()) {
            transaction = session.beginTransaction();
            session.save(examInstance);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateExamInstance(ExamInstance examInstance) {
        Transaction transaction = null;

        try (Session session = DatabaseManager.getSession()) {
            transaction = session.beginTransaction();
            session.merge(examInstance);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }



    public List<ExamInstance> getExamInstancesForTeacher(String teacherId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT i FROM ExamInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "LEFT JOIN FETCH e.examQuestions eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE i.administeringTeacher.userId = :teacherId " +
                                    "ORDER BY i.openingTime DESC",
                            ExamInstance.class
                    )
                    .setParameter("teacherId", teacherId)
                    .getResultList();
        }
    }

    public ExamInstance getExamInstanceById(Long instanceId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT i FROM ExamInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "LEFT JOIN FETCH e.examQuestions eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE i.instanceId = :instanceId",
                            ExamInstance.class
                    )
                    .setParameter("instanceId", instanceId)
                    .uniqueResult();
        }
    }



    public ExamInstance getAvailableInstanceByCode(String executionCode, LocalDateTime currentTime) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT i FROM ExamInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "LEFT JOIN FETCH e.examQuestions eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE i.executionCode = :executionCode " +
                                    "AND i.openingTime <= :currentTime " +
                                    "AND i.closingTime >= :currentTime",
                            ExamInstance.class
                    )
                    .setParameter("executionCode", executionCode)
                    .setParameter("currentTime", currentTime)
                    .uniqueResult();
        }
    }


    public boolean hasOverlappingExecutionCode(String executionCode, LocalDateTime openingTime,
                                               LocalDateTime closingTime) {
        try (Session session = DatabaseManager.getSession()) {
            Long matchingInstances = session.createQuery(
                            "SELECT COUNT(i) FROM ExamInstance i " +
                                    "WHERE i.executionCode = :executionCode " +
                                    "AND i.openingTime < :closingTime " +
                                    "AND i.closingTime > :openingTime",
                            Long.class
                    )
                    .setParameter("executionCode", executionCode)
                    .setParameter("openingTime", openingTime)
                    .setParameter("closingTime", closingTime)
                    .uniqueResult();

            return matchingInstances != null && matchingInstances > 0;
        }
    }
}
