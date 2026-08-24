package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.*;
import il.cshaifasweng.hsts.entities.view.ExamQuestionView;
import il.cshaifasweng.hsts.entities.view.ExamView;
import il.cshaifasweng.hsts.server.repositories.CourseRepository;
import il.cshaifasweng.hsts.server.repositories.ExamRepository;
import il.cshaifasweng.hsts.server.repositories.QuestionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamCreationServerLogic {
    private CourseRepository courseRepository;
    private QuestionRepository questionRepository;
    private ExamRepository examRepository;


    public ExamCreationServerLogic(CourseRepository courseRepository,
                                   QuestionRepository questionRepository, ExamRepository examRepository){
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }


    public boolean createExam(CurrentSession currentSession, ExamView examView){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())
                || !(currentSession.getCurrentUser() instanceof Teacher)){
            throw new IllegalStateException("Only teachers can create exams");
        }
        if(examView == null){
            throw new IllegalStateException("Exam data is missing");
        }
        if(examView.getCourseId() == null || examView.getCourseId().isBlank()){
            throw new IllegalStateException("Course ID is missing");
        }
        if (examView.getExamId() == null || !examView.getExamId().matches("\\d{6}")){
            throw new IllegalStateException("Exam ID must contain exactly six digits");
        }
        if(examView.getDuration() <= 0){
            throw new IllegalStateException("Exam duration must be positive");
        }
        if(examRepository.getExamById(examView.getExamId()) != null){
            throw new IllegalStateException("Exam ID already exists");
        }
        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        Course selectedCourse = null;
        for(Course course : teacherCourses){
            if(course.getCourseId().equals(examView.getCourseId())){
                selectedCourse = course;
                break;
            }
        }
        if(selectedCourse == null){
            throw new IllegalStateException("Teacher does not teach the selected course");
        }
        if(examView.getExamQuestions() == null || examView.getExamQuestions().isEmpty()){
            throw new IllegalStateException("Exam must contain at least one question");
        }
        Set<String> questionIds = new HashSet<>();
        int totalPoints = 0;

        for(ExamQuestionView questionView : examView.getExamQuestions()){
            if(questionView == null){
                throw new IllegalStateException("Question data is missing");
            }
            if(questionView.getQuestionId() == null || questionView.getQuestionId().isBlank()){
                throw new IllegalStateException("Question ID is missing");
            }
            if(!questionIds.add(questionView.getQuestionId())){
                throw new IllegalStateException("The same question cannot appear more than once");
            }

            Question question = questionRepository.getQuestionById(questionView.getQuestionId());
            if(question == null){
                throw new IllegalStateException("Question does not exist");
            }
            if(!selectedCourse.getCourseId().equals(question.getCourse().getCourseId())){
                throw new IllegalStateException("Question does not belong to the selected course");
            }
            if(questionView.getPoints() <= 0){
                throw new IllegalStateException("Question points must be positive");
            }

            totalPoints += questionView.getPoints();
        }

        if(totalPoints != 100){
            throw new IllegalStateException("Exam points must total 100");
        }


        Teacher teacher = (Teacher) currentSession.getCurrentUser();

        Exam exam = new Exam(selectedCourse, teacher, examView.getExamId(), examView.getDuration(),
                examView.getStudentInstructions(), examView.getTeacherInstructions());

        for(ExamQuestionView examQuestionView : examView.getExamQuestions()){
            Question question = questionRepository.getQuestionById(examQuestionView.getQuestionId());
            ExamQuestion examQuestion = new ExamQuestion(question, examQuestionView.getPoints());
            exam.addExamQuestion(examQuestion);
        }
        exam.submitForApproval();
        return examRepository.saveExam(exam);

    }












}
