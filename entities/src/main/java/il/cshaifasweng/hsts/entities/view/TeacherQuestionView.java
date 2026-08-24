package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class TeacherQuestionView implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseId;

    private String questionId;
    private String description;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correctAnswer;
    private String illustrationPath;


    public TeacherQuestionView(String courseId, String questionId, String description, String answer1, String answer2,
                               String answer3, String answer4, int correctAnswer, String illustrationPath){
        this.courseId = courseId;
        this.questionId = questionId;
        this.description = description;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.illustrationPath = illustrationPath;

    }


    //GETTERS//
    public String getCourseId() {
        return courseId;
    }

    public String getQuestionId(){
        return this.questionId;
    }

    public String getDescription() {
        return description;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getIllustrationPath() {
        return illustrationPath;
    }



//SETTERS//


    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setIllustrationPath(String illustrationPath) {
        this.illustrationPath = illustrationPath;
    }

}

