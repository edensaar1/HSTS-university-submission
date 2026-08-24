package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.*;
import il.cshaifasweng.hsts.entities.enums.ExamStatus;
import il.cshaifasweng.hsts.entities.view.ExamQuestionView;
import il.cshaifasweng.hsts.entities.view.ExamView;
import il.cshaifasweng.hsts.server.repositories.ExamRepository;

import java.util.ArrayList;
import java.util.List;

public class ExamApprovalServerLogic {
    private ExamRepository examRepository;

    public ExamApprovalServerLogic(ExamRepository examRepository){
        this.examRepository = examRepository;
    }


    public List<ExamView> getPendingExams(CurrentSession currentSession){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"SubjectCoordinator".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof SubjectCoordinator)){
            throw new IllegalStateException("Only Subject coordinator can receive pending exams");
        }
        List<Exam> pendingExams = examRepository.getPendingExamsForCoordinator(currentSession.getUserId());
        List<ExamView> pendingExamViews = new ArrayList<>();
        for(Exam exam : pendingExams){
            Course course = exam.getCourse();
            Teacher teacher = exam.getTeacher();

            ExamView examView = new ExamView(course.getCourseId(), course.getCourseName(), teacher.getUserId(),
                    teacher.getFullName(), exam.getExamId(), exam.getDuration(), exam.getStudentInstructions(),
                    exam.getTeacherInstructions());
            examView.setStatus(exam.getStatus());
            examView.setRejectionReason(exam.getRejectionReason());

            List <ExamQuestionView> examQuestionViews = new ArrayList<>();
            for(ExamQuestion question : exam.getExamQuestions()){
                ExamQuestionView examQuestionView = new ExamQuestionView(
                        question.getQuestion().getQuestionId(), question.getPoints());
                examQuestionViews.add(examQuestionView);
            }
            examView.setExamQuestions(examQuestionViews);
            pendingExamViews.add(examView);
        }
        return pendingExamViews;
    }

    public synchronized boolean approveExam(CurrentSession currentSession, String examId){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"SubjectCoordinator".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof SubjectCoordinator)){
            throw new IllegalStateException("Only Subject coordinator can approve pending exams");
        }
        if(examId == null || examId.isBlank()){
            throw new IllegalStateException("Exam ID is missing");
        }
        Exam exam = examRepository.getExamById(examId);
        if(exam == null){
            throw new IllegalStateException("Exam does not exist");
        }
        if(!exam.getCourse().getSubjectCoordinator().getUserId().equals(currentSession.getUserId())){
            throw new IllegalStateException("Only Subject coordinator can approve pending exams");
        }
        if(exam.getStatus() != ExamStatus.PENDING_APPROVAL){
            throw new IllegalStateException("Exam is no longer pending approval");
        }
        exam.approve();
        return examRepository.updateExam(exam);
    }

    public synchronized boolean rejectExam(CurrentSession currentSession, String examId, String reason){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"SubjectCoordinator".equals(currentSession.getRole()) ||
                !(currentSession.getCurrentUser() instanceof SubjectCoordinator)){
            throw new IllegalStateException("Only Subject coordinator can reject pending exams");
        }
        if(examId == null || examId.isBlank()){
            throw new IllegalStateException("Exam ID is missing");
        }
        Exam exam = examRepository.getExamById(examId);
        if(exam == null){
            throw new IllegalStateException("Exam does not exist");
        }
        if(!exam.getCourse().getSubjectCoordinator().getUserId().equals(currentSession.getUserId())){
            throw new IllegalStateException("Only Subject coordinator can reject pending exams");
        }
        if(exam.getStatus() != ExamStatus.PENDING_APPROVAL){
            throw new IllegalStateException("Exam is no longer pending approval");
        }
        exam.reject(reason);
        return examRepository.updateExam(exam);
    }

}
