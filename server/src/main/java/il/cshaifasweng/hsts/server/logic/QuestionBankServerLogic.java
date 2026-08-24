package il.cshaifasweng.hsts.server.logic;

import il.cshaifasweng.hsts.entities.Course;
import il.cshaifasweng.hsts.entities.CurrentSession;
import il.cshaifasweng.hsts.entities.Question;
import il.cshaifasweng.hsts.entities.view.CourseView;
import il.cshaifasweng.hsts.entities.view.TeacherQuestionView;
import il.cshaifasweng.hsts.server.repositories.CourseRepository;
import il.cshaifasweng.hsts.server.repositories.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

public class QuestionBankServerLogic {
    private QuestionRepository questionRepository;
    private CourseRepository courseRepository;

    public QuestionBankServerLogic(QuestionRepository qRepo, CourseRepository cRepo){
        this.questionRepository = qRepo;
        this.courseRepository = cRepo;
    }





    public List<CourseView> getTeacherCourses(CurrentSession currentSession){
        if(currentSession == null){
            throw new  IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())){
            throw new  IllegalStateException("Only teachers can access the question bank");
        }
        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        List<CourseView> courseViewList = new ArrayList<>();
        for(Course course : teacherCourses) {
            courseViewList.add(new CourseView(course.getCourseId(), course.getCourseName()));
        }
        return courseViewList;
    }





    public List<TeacherQuestionView> getCourseQuestions(CurrentSession currentSession, String courseId){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())){
            throw new IllegalStateException("Only teachers can access the question bank");
        }
        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        boolean flag = false;
        for(Course course : teacherCourses){
            if(course.getCourseId().equals(courseId)){
                flag = true;
                break;
            }
        }
        if (!flag){
            throw new IllegalStateException("Course doesn't belong to you!");
        }
        List<Question> questions = questionRepository.getCourseQuestions(courseId);
        List<TeacherQuestionView> teacherQuestionViews = new ArrayList<>();

        for(Question q : questions){
            teacherQuestionViews.add(new TeacherQuestionView(courseId, q.getQuestionId(), q.getDescription(),
                    q.getAnswer1(), q.getAnswer2(), q.getAnswer3(), q.getAnswer4(), q.getCorrectAnswer(),
                    q.getIllustrationPath()));
        }
        return teacherQuestionViews;
    }






    public boolean createQuestion(CurrentSession currentSession, TeacherQuestionView teacherQview){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())){
            throw new IllegalStateException("Only teachers can access the question bank");
        }

        if (teacherQview == null){
            throw new IllegalStateException("Question is missing");
        }
        if(teacherQview.getCourseId() == null || teacherQview.getCourseId().isBlank()){
            throw new IllegalStateException("Course ID is missing");
        }
        if(teacherQview.getQuestionId() == null ||teacherQview.getQuestionId().isBlank()){
            throw new IllegalStateException("Question ID is missing");
        }
        if(teacherQview.getQuestionId().length() != 5){
            throw new IllegalStateException("Question ID is wrong format");
        }
        if(!teacherQview.getQuestionId().matches("\\d{5}")){
            throw new IllegalStateException("Question ID must contain exactly five digits");
        }
        if(teacherQview.getDescription() == null || teacherQview.getDescription().isBlank()){
            throw new IllegalStateException("Question description is missing");
        }
        if(teacherQview.getAnswer1() == null || teacherQview.getAnswer1().isBlank()
                || teacherQview.getAnswer2() == null || teacherQview.getAnswer2().isBlank()
                || teacherQview.getAnswer3() == null || teacherQview.getAnswer3().isBlank()
                || teacherQview.getAnswer4() == null || teacherQview.getAnswer4().isBlank()){
            throw new IllegalStateException("All four answers are required");
        }
        if(teacherQview.getCorrectAnswer() < 1 || teacherQview.getCorrectAnswer() > 4){
            throw new IllegalStateException("Correct answer must be between 1 and 4");
        }

        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        Course selectedCourse = null;
        for(Course course : teacherCourses){
            if(course.getCourseId().equals(teacherQview.getCourseId())){
                selectedCourse = course;
                break;
            }
        }
        if(selectedCourse == null){
            throw new IllegalStateException("Course doesn't belong to you");
        }
        if(!teacherQview.getQuestionId().substring(0, 2)
                .equals(selectedCourse.getSubject().getSubjectId())){
            throw new IllegalStateException("Question ID subject code does not match the course subject");
        }
        if(questionRepository.getQuestionById(teacherQview.getQuestionId()) != null){
            throw new IllegalStateException("Question ID already exists");
        }



        //passed all checks//
        Question question = new Question(selectedCourse, teacherQview.getQuestionId(), teacherQview.getDescription(),
                            teacherQview.getAnswer1(), teacherQview.getAnswer2(), teacherQview.getAnswer3(),
                            teacherQview.getAnswer4(), teacherQview.getCorrectAnswer(),
                            teacherQview.getIllustrationPath());
        return questionRepository.createQuestion(question);

    }



    public boolean updateQuestion(CurrentSession currentSession, TeacherQuestionView teacherQview){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())){
            throw new IllegalStateException("Only teachers can access the question bank");
        }
        if (teacherQview == null){
            throw new IllegalStateException("Question is missing");
        }

        if(teacherQview.getQuestionId() == null || teacherQview.getQuestionId().isBlank()){
            throw new IllegalStateException("Question ID is missing");
        }
        if(!teacherQview.getQuestionId().matches("\\d{5}")){
            throw new IllegalStateException("Question ID must contain exactly five digits");
        }
        if(teacherQview.getDescription() == null || teacherQview.getDescription().isBlank()){
            throw new IllegalStateException("Question description is missing");
        }
        if(teacherQview.getAnswer1() == null || teacherQview.getAnswer1().isBlank()
                || teacherQview.getAnswer2() == null || teacherQview.getAnswer2().isBlank()
                || teacherQview.getAnswer3() == null || teacherQview.getAnswer3().isBlank()
                || teacherQview.getAnswer4() == null || teacherQview.getAnswer4().isBlank()){
            throw new IllegalStateException("All four answers are required");
        }
        if(teacherQview.getCorrectAnswer() < 1 || teacherQview.getCorrectAnswer() > 4){
            throw new IllegalStateException("Correct answer must be between 1 and 4");
        }

        Question question = questionRepository.getQuestionById(teacherQview.getQuestionId());
        if(question == null){
            throw new IllegalStateException("Question does not exist");
        }
        if(teacherQview.getCourseId() == null || teacherQview.getCourseId().isBlank()){
            throw new IllegalStateException("Course ID is missing");
        }
        if(!teacherQview.getCourseId().equals(question.getCourse().getCourseId())){
            throw new IllegalStateException("Question cannot be moved to another course");
        }

        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        boolean ownsCourse = false;
        for(Course course : teacherCourses){
            if(course.getCourseId().equals(question.getCourse().getCourseId())){
                ownsCourse = true;
                break;
            }
        }
        if(!ownsCourse){
            throw new IllegalStateException("Course doesn't belong to you");
        }

        question.setDescription(teacherQview.getDescription());
        question.setAnswer1(teacherQview.getAnswer1());
        question.setAnswer2(teacherQview.getAnswer2());
        question.setAnswer3(teacherQview.getAnswer3());
        question.setAnswer4(teacherQview.getAnswer4());
        question.setCorrectAnswer(teacherQview.getCorrectAnswer());
        question.setIllustrationPath(teacherQview.getIllustrationPath());

        return questionRepository.updateQuestion(question);

    }

    public boolean deleteQuestion(CurrentSession currentSession, String questionId){
        if(currentSession == null) {
            throw new IllegalStateException("User is not logged in");
        }
        if(!"Teacher".equals(currentSession.getRole())){
            throw new IllegalStateException("Only teachers can access the question bank");
        }
        if(questionId == null || questionId.isBlank()){
            throw new IllegalStateException("Question ID is missing");
        }
        if(!questionId.matches("\\d{5}")){
            throw new IllegalStateException("Question ID must contain exactly five digits");
        }

        Question q = questionRepository.getQuestionById(questionId);
        if(q == null){
            throw new IllegalStateException("Question doesn't exist!");
        }

        List<Course> teacherCourses = courseRepository.getTeacherCourses(currentSession.getUserId());
        boolean ownsCourse = false;
        for(Course course : teacherCourses){
            if(course.getCourseId().equals(q.getCourse().getCourseId())){
                ownsCourse = true;
                break;
            }
        }
        if(!ownsCourse){
            throw new IllegalStateException("Course doesn't belong to you");
        }

        return questionRepository.deleteQuestion(q);

    }























}
