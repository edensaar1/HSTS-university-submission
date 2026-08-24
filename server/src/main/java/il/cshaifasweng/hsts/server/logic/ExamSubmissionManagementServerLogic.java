package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.*;
import il.cshaifasweng.hsts.entities.enums.ExamSubmissionStatus;
import il.cshaifasweng.hsts.entities.view.ExamSubmissionView;
import il.cshaifasweng.hsts.entities.view.SubmissionReviewAnswerView;
import il.cshaifasweng.hsts.server.repositories.ExamRepository;
import il.cshaifasweng.hsts.server.repositories.ExamSubmissionsRepository;
import il.cshaifasweng.hsts.server.repositories.ExaminationRepository;

import java.util.ArrayList;
import java.util.List;

public class ExamSubmissionManagementServerLogic {
    private final ExamRepository examRepository;
    private final ExaminationRepository examinationRepository;
    private final ExamSubmissionsRepository examSubmissionsRepository;

    public ExamSubmissionManagementServerLogic(ExamRepository examRepository,
            ExaminationRepository examinationRepository, ExamSubmissionsRepository examSubmissionsRepository) {
        this.examRepository = examRepository;
        this.examinationRepository = examinationRepository;
        this.examSubmissionsRepository = examSubmissionsRepository;
    }



    public int calculateAutomaticGrade(ExamSubmission submission){
        int totalGrade = 0;
        if(submission == null){
            throw new IllegalArgumentException("Submission cannot be null");
        }
        if(submission.getStatus() != ExamSubmissionStatus.SUBMITTED &&
                submission.getStatus() != ExamSubmissionStatus.TIMED_OUT){
            throw new IllegalStateException("Only submitted or timed-out exams can be graded");
        }
        if (submission.getAutomaticGrade() != null){
            throw new IllegalStateException("Submission has already been automatically graded");
        }

        for(SubmissionAnswer submissionAnswer : submission.getAnswers()){
            ExamQuestion examQuestion = submissionAnswer.getExamQuestion();
            Question question = examQuestion.getQuestion();
            if(submissionAnswer.getSelectedAnswer() == question.getCorrectAnswer()){
                totalGrade += examQuestion.getPoints();
            }
        }
        submission.setAutomaticGrade(totalGrade);
        submission.setFinalGrade(totalGrade);
        return totalGrade;
    }


    public int gradeSubmission(CurrentSession currentSession, Long submissionId){
        if(currentSession == null){
            throw new IllegalArgumentException("Current session cannot be null");
        }
        if(!"Teacher".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof Teacher)){
            throw new IllegalStateException("Only teachers can grade exam submissions");
        }
        if(submissionId == null){
            throw new IllegalArgumentException("Submission ID cannot be null");
        }
        ExamSubmission examSubmission = examSubmissionsRepository.getSubmissionById(submissionId);
        if(examSubmission == null){
            throw new IllegalArgumentException("Submission was not found");
        }
        if(!examSubmissionsRepository.teacherTeachesSubmissionCourse(
                currentSession.getUserId(), submissionId)){
            throw new IllegalStateException("Teacher does not teach the submission course");
        }
        int grade = calculateAutomaticGrade(examSubmission);
        if(!examSubmissionsRepository.updateSubmission(examSubmission)){
            throw new IllegalStateException("Could not save the calculated grade");
        }

        return grade;
    }




    public List<ExamSubmissionView> getSubmissionsForReview(CurrentSession currentSession){
        if(currentSession == null){
            throw new IllegalArgumentException("Current session cannot be null");
        }
        if(!"Teacher".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof Teacher)){
            throw new IllegalStateException("Only teachers can review exam submissions");
        }

        List<ExamSubmission> submissions =
                examSubmissionsRepository.getSubmissionsForTeacher(currentSession.getUserId());
        List<ExamSubmissionView> submissionViews = new ArrayList<>();

        for(ExamSubmission submission : submissions){
            if(submission.getAutomaticGrade() == null){
                calculateAutomaticGrade(submission);
                if(!examSubmissionsRepository.updateSubmission(submission)){
                    throw new IllegalStateException("Could not save the calculated grade");
                }
            }

            List<SubmissionReviewAnswerView> answerViews = new ArrayList<>();
            for(SubmissionAnswer answer : submission.getAnswers()){
                ExamQuestion examQuestion = answer.getExamQuestion();
                Question question = examQuestion.getQuestion();

                answerViews.add(new SubmissionReviewAnswerView(
                        examQuestion.getId(), question.getQuestionId(), question.getDescription(),
                        question.getAnswer1(), question.getAnswer2(), question.getAnswer3(), question.getAnswer4(),
                        answer.getSelectedAnswer(), question.getCorrectAnswer(), examQuestion.getPoints()));
            }

            Exam exam = submission.getExamInstance().getExam();
            Student student = submission.getStudent();
            submissionViews.add(new ExamSubmissionView(
                    submission.getSubmissionId(), submission.getExamInstance().getInstanceId(), exam.getExamId(),
                    exam.getCourse().getCourseName(), student.getUserId(), student.getFullName(),
                    submission.getSubmittedAt(), submission.getActualDurationMinutes(), submission.getStatus().name(),
                    submission.getAutomaticGrade(), submission.getFinalGrade(), submission.getTeacherComment(),
                    submission.getGradeChangeReason(), answerViews));
        }

        return submissionViews;
    }


    public synchronized boolean approveSubmission(CurrentSession currentSession, Long submissionId, int finalGrade,
                                                  String teacherComment, String gradeChangeReason){
        if(currentSession == null){
            throw new IllegalArgumentException("Current session cannot be null");
        }
        if(!"Teacher".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof Teacher)){
            throw new IllegalStateException("Only teachers can review exam submissions");
        }
        if(submissionId == null){
            throw new IllegalArgumentException("Submission ID cannot be null");
        }
        ExamSubmission examSubmission = examSubmissionsRepository.getSubmissionById(submissionId);
        if(examSubmission == null){
            throw new IllegalArgumentException("Submission was not found");
        }
        if(examSubmission.isApproved()){
            throw new IllegalStateException("Submission has already been approved");
        }

        if(!examSubmissionsRepository.teacherTeachesSubmissionCourse(
                currentSession.getUserId(), submissionId)){
            throw new IllegalStateException("Teacher does not teach the submission course");
        }
        if(examSubmission.getStatus() != ExamSubmissionStatus.SUBMITTED &&
                examSubmission.getStatus() != ExamSubmissionStatus.TIMED_OUT){
            throw new IllegalStateException("Only submitted or timed-out exams can be graded");
        }

        if(examSubmission.getAutomaticGrade() == null){
            throw new IllegalStateException("Submission must be automatically graded before approval");
        }
        if(finalGrade < 0 || finalGrade > 100){
            throw new IllegalArgumentException("Final grade must be between 0 and 100");
        }
        if(examSubmission.getAutomaticGrade() != finalGrade && ( gradeChangeReason == null
        || gradeChangeReason.isBlank())){
            throw new IllegalArgumentException("A reason is required when changing the automatic grade");
        }
        examSubmission.setFinalGrade(finalGrade);
        examSubmission.setTeacherComment(teacherComment);
        if(examSubmission.getAutomaticGrade() != finalGrade){
            examSubmission.setGradeChangeReason(gradeChangeReason.trim());
        } else {
            examSubmission.setGradeChangeReason(null);
        }
        examSubmission.setApproved(true);
        return examSubmissionsRepository.updateSubmission(examSubmission);
    }
}
