package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.*;
import il.cshaifasweng.hsts.entities.enums.ExamSubmissionStatus;
import il.cshaifasweng.hsts.entities.view.ExamExecutionView;
import il.cshaifasweng.hsts.entities.view.StudentQuestionView;
import il.cshaifasweng.hsts.entities.view.SubmissionAnswerView;
import il.cshaifasweng.hsts.server.repositories.CourseRepository;
import il.cshaifasweng.hsts.server.repositories.ExamSubmissionsRepository;
import il.cshaifasweng.hsts.server.repositories.ExaminationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExamExecutionServerLogic {
    private CourseRepository courseRepository;
    private ExaminationRepository examinationRepository;
    private ExamSubmissionsRepository examSubmissionsRepository;

    public ExamExecutionServerLogic(CourseRepository courseRepository ,
                                    ExaminationRepository examinationRepository,
                                    ExamSubmissionsRepository examSubmissionsRepository){
        this.courseRepository = courseRepository;
        this.examinationRepository = examinationRepository;
        this.examSubmissionsRepository = examSubmissionsRepository;
    }

    public ExamExecutionView startExam(CurrentSession currentSession, String executionCode, String studentId){
        if(currentSession == null){
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Student".equals(currentSession.getRole()) || !(currentSession.getCurrentUser() instanceof Student)){
            throw new IllegalStateException("Only student can start exam");
        }
        if(executionCode == null || !executionCode.trim().matches("[A-Za-z0-9]{4}")){
            throw new IllegalArgumentException("Execution code must contain exactly four letters or digits");
        }
        if(studentId == null || studentId.isBlank()){
            throw new IllegalArgumentException("Student ID is required");
        }
        if(!studentId.equals(currentSession.getUserId())){
            throw new IllegalArgumentException("Student ID does not match the logged-in user");
        }
        executionCode = executionCode.trim().toUpperCase(Locale.ROOT);
        ExamInstance examInstance = examinationRepository.getAvailableInstanceByCode(executionCode,LocalDateTime.now());
        if(examInstance == null){
            throw new IllegalArgumentException("No available exam was found for this execution code");
        }
        List<Course> courseList = courseRepository.getStudentCourses(studentId);
        boolean enrolled = false;
        for(Course course : courseList){
            if((examInstance.getExam().getCourse().getCourseId()).equals(course.getCourseId())){
                enrolled = true;
                break;
            }
        }
        if(!enrolled){
            throw new IllegalStateException("Student is not enrolled in this exam's course");
        }
        ExamSubmission existingSubmission = examSubmissionsRepository.getStudentSubmission(
                examInstance.getInstanceId(), studentId);
        if(existingSubmission != null){
            throw new IllegalStateException("Student has already started this exam");
        }
        Student student = (Student) currentSession.getCurrentUser();
        ExamSubmission examSubmission = new ExamSubmission(examInstance, student, LocalDateTime.now());
        if(!examSubmissionsRepository.saveSubmission(examSubmission)){
            throw new IllegalStateException("Could not save the exam submission");
        }

        List<StudentQuestionView> questionViews = new ArrayList<>();
        for(ExamQuestion examQuestion : examInstance.getExam().getExamQuestions()){
            Question question = examQuestion.getQuestion();
            StudentQuestionView questionView = new StudentQuestionView(examQuestion.getId(), question.getQuestionId(),
                    question.getDescription(), question.getAnswer1(), question.getAnswer2(), question.getAnswer3(),
                    question.getAnswer4(), question.getIllustrationPath(), examQuestion.getPoints());

            questionViews.add(questionView);
        }
        return new ExamExecutionView(examInstance.getInstanceId(), examSubmission.getSubmissionId(), examInstance.getExam().getExamId(),
                examInstance.getExam().getCourse().getCourseName(), examInstance.getExam().getStudentInstructions(),
                examInstance.getEffectiveDuration(), examSubmission.getStartedAt(), questionViews);
    }





    public boolean submitExam(CurrentSession currentSession, Long submissionId, List<SubmissionAnswerView> answerViews){
        if(currentSession == null){
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Student".equals(currentSession.getRole()) || !(currentSession.getCurrentUser() instanceof Student)){
            throw new IllegalStateException("Only students can submit an exam");
        }
        if(submissionId == null){
            throw new IllegalArgumentException("Submission ID is required");
        }
        if(answerViews == null){
            throw new IllegalArgumentException("Answer list is required");
        }
        ExamSubmission currentSubmission = examSubmissionsRepository.getSubmissionById(submissionId);
        if(currentSubmission == null){
            throw new IllegalArgumentException("Submission was not found");
        }
        if(!currentSubmission.getStudent().getUserId().equals(currentSession.getCurrentUser().getUserId())){
            throw new IllegalStateException("Submission does not belong to the logged-in student");
        }
        if(currentSubmission.getStatus() != ExamSubmissionStatus.IN_PROGRESS){
            throw new IllegalStateException("Exam has already been submitted");
        }
        LocalDateTime submittedAt = LocalDateTime.now();
        if(submittedAt.isAfter(currentSubmission.getStartedAt().plusMinutes(currentSubmission.getExamInstance().getEffectiveDuration()))){
            throw new IllegalStateException("Exam time has expired");
        }
        List<ExamQuestion> examQuestions = currentSubmission.getExamInstance().getExam().getExamQuestions();
        List<Integer> submittedQuestionIds = new ArrayList<>();
        List<ExamQuestion> answeredQuestions = new ArrayList<>();
        for(SubmissionAnswerView answerView : answerViews){
            if(answerView == null || answerView.getSelectedAnswer() < 1 || answerView.getSelectedAnswer() > 4){
                throw new IllegalArgumentException("Selected answer must be between 1 and 4");
            }
            if(submittedQuestionIds.contains(answerView.getExamQuestionId())){
                throw new IllegalArgumentException("The same question cannot be submitted twice");
            }
            ExamQuestion matchingQuestion = null;
            for(ExamQuestion examQuestion : examQuestions){
                if(examQuestion.getId() == answerView.getExamQuestionId()){
                    matchingQuestion = examQuestion;
                    break;
                }
            }
            if(matchingQuestion == null){
                throw new IllegalArgumentException("Submitted question does not belong to this exam");
            }
            submittedQuestionIds.add(answerView.getExamQuestionId());
            answeredQuestions.add(matchingQuestion);
        }
        for(int i = 0; i < answerViews.size(); i++){
            SubmissionAnswer submissionAnswer = new SubmissionAnswer(answeredQuestions.get(i),
                    answerViews.get(i).getSelectedAnswer());
            currentSubmission.addAnswer(submissionAnswer);
        }

        long duration = Duration.between(currentSubmission.getStartedAt(), submittedAt).toMinutes();
        currentSubmission.setSubmittedAt(submittedAt);
        currentSubmission.setActualDurationMinutes((int) duration);
        currentSubmission.setStatus(ExamSubmissionStatus.SUBMITTED);

        return examSubmissionsRepository.updateSubmission(currentSubmission);
    }

    public boolean timeoutExam(CurrentSession currentSession, Long submissionId,List<SubmissionAnswerView> answerViews){
        if(currentSession == null){
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Student".equals(currentSession.getRole()) || !(currentSession.getCurrentUser() instanceof Student)){
            throw new IllegalStateException("Only students can time out an exam");
        }
        if(submissionId == null){
            throw new IllegalArgumentException("Submission ID is required");
        }
        if(answerViews == null){
            throw new IllegalArgumentException("Answer list is required");
        }
        ExamSubmission currentSubmission = examSubmissionsRepository.getSubmissionById(submissionId);
        if(currentSubmission == null){
            throw new IllegalArgumentException("Submission was not found");
        }
        if(!currentSubmission.getStudent().getUserId().equals(currentSession.getUserId())){
            throw new IllegalStateException("Submission does not belong to the logged-in student");
        }
        if(currentSubmission.getStatus() != ExamSubmissionStatus.IN_PROGRESS){
            throw new IllegalStateException("Exam has already been submitted");
        }
        LocalDateTime deadline = currentSubmission.getStartedAt().plusMinutes(
                currentSubmission.getExamInstance().getEffectiveDuration());
        if(LocalDateTime.now().isBefore(deadline)){
            throw new IllegalStateException("Exam time has not expired");
        }

        List<ExamQuestion> examQuestions = currentSubmission.getExamInstance().getExam().getExamQuestions();
        List<Integer> submittedQuestionIds = new ArrayList<>();
        List<ExamQuestion> answeredQuestions = new ArrayList<>();

        for(SubmissionAnswerView answerView : answerViews){
            if(answerView == null || answerView.getSelectedAnswer() < 1 || answerView.getSelectedAnswer() > 4){
                throw new IllegalArgumentException("Selected answer must be between 1 and 4");
            }
            if(submittedQuestionIds.contains(answerView.getExamQuestionId())){
                throw new IllegalArgumentException("The same question cannot be submitted twice");
            }
            ExamQuestion matchingQuestion = null;
            for(ExamQuestion examQuestion : examQuestions){
                if(examQuestion.getId() == answerView.getExamQuestionId()){
                    matchingQuestion = examQuestion;
                    break;
                }
            }
            if(matchingQuestion == null){
                throw new IllegalArgumentException("Submitted question does not belong to this exam");
            }
            submittedQuestionIds.add(answerView.getExamQuestionId());
            answeredQuestions.add(matchingQuestion);
        }
        for(int i = 0; i < answerViews.size(); i++){
            SubmissionAnswer submissionAnswer = new SubmissionAnswer(answeredQuestions.get(i),
                    answerViews.get(i).getSelectedAnswer());
            currentSubmission.addAnswer(submissionAnswer);
        }

        currentSubmission.setSubmittedAt(deadline);
        currentSubmission.setActualDurationMinutes(currentSubmission.getExamInstance().getEffectiveDuration());
        currentSubmission.setStatus(ExamSubmissionStatus.TIMED_OUT);

        return examSubmissionsRepository.updateSubmission(currentSubmission);
    }



}
