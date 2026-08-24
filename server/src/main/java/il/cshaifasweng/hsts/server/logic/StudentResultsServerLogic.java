package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.*;
import il.cshaifasweng.hsts.entities.view.StudentResultView;
import il.cshaifasweng.hsts.entities.view.SubmissionReviewAnswerView;
import il.cshaifasweng.hsts.server.repositories.ExamSubmissionsRepository;

import java.util.ArrayList;
import java.util.List;

public class StudentResultsServerLogic {
    private final ExamSubmissionsRepository examSubmissionsRepository;

    public StudentResultsServerLogic(
            ExamSubmissionsRepository examSubmissionsRepository) {
        this.examSubmissionsRepository = examSubmissionsRepository;
    }




    public List<StudentResultView> getStudentResults(CurrentSession currentSession){
        if(currentSession == null){
            throw new IllegalArgumentException("Current session cannot be null");
        }
        if(!"Student".equals(currentSession.getRole()) || !(currentSession.getCurrentUser() instanceof Student)){
            throw new IllegalStateException("Only students can view student results");
        }
        List<ExamSubmission> examSubmissions = examSubmissionsRepository
                .getApprovedResultsForStudent(currentSession.getUserId());

        List<StudentResultView> studentResultViews = new ArrayList<>();

        for(ExamSubmission examSubmission : examSubmissions){
            if(examSubmission.getFinalGrade() == null){
                throw new IllegalStateException("Approved submission is missing its final grade");
            }
            List<SubmissionAnswer> answers = examSubmission.getAnswers();

            List<SubmissionReviewAnswerView> submissionReviewAnswerViews = new ArrayList<>();

            StudentResultView studentResultView = new StudentResultView(examSubmission.getSubmissionId(),
                    examSubmission.getExamInstance().getExam().getExamId(), examSubmission.getExamInstance().
                    getExam().getCourse().getCourseName(), examSubmission.getSubmittedAt(), examSubmission.
                    getActualDurationMinutes(), examSubmission.getStatus().name(), examSubmission.
                    getFinalGrade(), examSubmission.getTeacherComment(), submissionReviewAnswerViews);

            for(SubmissionAnswer answer : answers){
                SubmissionReviewAnswerView answerView = new SubmissionReviewAnswerView(answer.getExamQuestion().getId()
                , answer.getExamQuestion().getQuestion().getQuestionId(), answer.getExamQuestion().getQuestion()
                        .getDescription(), answer.getExamQuestion().getQuestion().getAnswer1(),
                        answer.getExamQuestion().getQuestion().getAnswer2(),
                        answer.getExamQuestion().getQuestion().getAnswer3(),
                answer.getExamQuestion().getQuestion().getAnswer4(), answer.getSelectedAnswer(),
                        answer.getExamQuestion().getQuestion().getCorrectAnswer(), answer.getExamQuestion().getPoints()
                );
                studentResultView.getAnswers().add(answerView);
            }
            studentResultViews.add(studentResultView);
        }

        return studentResultViews;
    }











}
