package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.ExamInstance;
import il.cshaifasweng.hsts.entities.ExamSubmission;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class StatisticsRepository {
    public List<ExamInstance> getCompletedInstancesForExamAuthor(String teacherId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT i FROM ExamInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "JOIN FETCH e.course " +
                                    "JOIN FETCH i.administeringTeacher " +
                                    "WHERE e.teacher.userId = :teacherId " +
                                    "AND i.closingTime <= :currentTime " +
                                    "ORDER BY i.openingTime DESC",
                            ExamInstance.class
                    )
                    .setParameter("teacherId", teacherId)
                    .setParameter("currentTime", LocalDateTime.now())
                    .getResultList();
        }
    }

    public List<ExamSubmission> getSubmissionsForInstance(Long instanceId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "FROM ExamSubmission s " +
                                    "WHERE s.examInstance.instanceId = :instanceId " +
                                    "ORDER BY s.submissionId",
                            ExamSubmission.class
                    )
                    .setParameter("instanceId", instanceId)
                    .getResultList();
        }
    }
}
