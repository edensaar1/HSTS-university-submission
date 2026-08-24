package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class StudentQuestionView implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examQuestionId;
    private String questionId;
    private String text;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String illustrationPath;
    private int points;

    public StudentQuestionView(int examQuestionId, String questionId, String text, String answer1, String answer2, String answer3, String answer4, String illustrationPath, int points){
        this.examQuestionId = examQuestionId;
        this.questionId = questionId;
        this.text = text;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.illustrationPath = illustrationPath;
        this.points = points;
    }

    //GETTERS//

    public int getExamQuestionId() {
        return examQuestionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
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

    public String getIllustrationPath() {
        return illustrationPath;
    }

    public int getPoints() {
        return points;
    }

    //SETTERS//

    public void setExamQuestionId(int examQuestionId) {
        this.examQuestionId = examQuestionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setText(String text) {
        this.text = text;
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

    public void setIllustrationPath(String illustrationPath) {
        this.illustrationPath = illustrationPath;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
