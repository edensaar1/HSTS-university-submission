package il.cshaifasweng.hsts.server.repositories;

import il.cshaifasweng.hsts.entities.ExamSubmission;
import il.cshaifasweng.hsts.entities.enums.ExamSubmissionStatus;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ExamSubmissionsRepository {

    public boolean saveSubmission(ExamSubmission submission) {
        Transaction transaction = null;

        try (Session session = DatabaseManager.getSession()) {
            transaction = session.beginTransaction();
            session.save(submission);
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

    public boolean updateSubmission(ExamSubmission submission) {
        Transaction transaction = null;

        try (Session session = DatabaseManager.getSession()) {
            transaction = session.beginTransaction();
            session.merge(submission);
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

    public ExamSubmission getSubmissionById(Long submissionId) {
        try (Session session = DatabaseManager.getSession()) {
            ExamSubmission submission = session.createQuery(
                            "SELECT DISTINCT s FROM ExamSubmission s " + "LEFT JOIN FETCH s.answers a " +
                                    "LEFT JOIN FETCH a.examQuestion " + "WHERE s.submissionId = :submissionId",
                            ExamSubmission.class)
                    .setParameter("submissionId", submissionId).uniqueResult();

            if (submission != null) {
                submission.getExamInstance().getExam().getExamQuestions().size();
            }
            return submission;
        }
    }

    public ExamSubmission getStudentSubmission(Long instanceId, String studentId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM ExamSubmission s " +
                                    "LEFT JOIN FETCH s.answers a " +
                                    "LEFT JOIN FETCH a.examQuestion " +
                                    "WHERE s.examInstance.instanceId = :instanceId " +
                                    "AND s.student.userId = :studentId",
                            ExamSubmission.class
                    )
                    .setParameter("instanceId", instanceId)
                    .setParameter("studentId", studentId)
                    .uniqueResult();
        }
    }

    public List<ExamSubmission> getSubmissionsForTeacher(String teacherId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM ExamSubmission s " +
                                    "JOIN FETCH s.examInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "JOIN FETCH e.course c " +
                                    "JOIN FETCH s.student " +
                                    "LEFT JOIN FETCH s.answers a " +
                                    "LEFT JOIN FETCH a.examQuestion eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE c IN (" +
                                    "SELECT teacherCourse FROM Teacher t JOIN t.courses teacherCourse " +
                                    "WHERE t.userId = :teacherId) " +
                                    "AND s.status IN (:submittedStatus, :timedOutStatus) " +
                                    "AND s.approved = false " +
                                    "ORDER BY s.submittedAt DESC",
                            ExamSubmission.class
                    )
                    .setParameter("teacherId", teacherId)
                    .setParameter("submittedStatus", ExamSubmissionStatus.SUBMITTED)
                    .setParameter("timedOutStatus", ExamSubmissionStatus.TIMED_OUT)
                    .getResultList();
        }
    }

    public boolean teacherTeachesSubmissionCourse(String teacherId, Long submissionId) {
        try (Session session = DatabaseManager.getSession()) {
            Long matchingSubmissions = session.createQuery(
                            "SELECT COUNT(s) FROM ExamSubmission s " +
                                    "WHERE s.submissionId = :submissionId " +
                                    "AND s.examInstance.exam.course IN (" +
                                    "SELECT teacherCourse FROM Teacher t JOIN t.courses teacherCourse " +
                                    "WHERE t.userId = :teacherId)",
                            Long.class
                    )
                    .setParameter("submissionId", submissionId)
                    .setParameter("teacherId", teacherId)
                    .uniqueResult();
            return matchingSubmissions != null && matchingSubmissions > 0;
        }
    }

    public List<ExamSubmission> getApprovedResultsForStudent(String studentId) {
        try (Session session = DatabaseManager.getSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s FROM ExamSubmission s " +
                                    "JOIN FETCH s.examInstance i " +
                                    "JOIN FETCH i.exam e " +
                                    "JOIN FETCH e.course " +
                                    "LEFT JOIN FETCH s.answers a " +
                                    "LEFT JOIN FETCH a.examQuestion eq " +
                                    "LEFT JOIN FETCH eq.question " +
                                    "WHERE s.student.userId = :studentId " +
                                    "AND s.approved = true " +
                                    "ORDER BY s.submittedAt DESC",
                            ExamSubmission.class
                    )
                    .setParameter("studentId", studentId)
                    .getResultList();
        }
    }


}
