package il.cshaifasweng.hsts.entities.view;

import il.cshaifasweng.hsts.entities.Exam;
import il.cshaifasweng.hsts.entities.Question;

import java.io.Serializable;

public class ExamQuestionView implements Serializable {

    private String examId;
    private String questionId;

    private int points;



    public ExamQuestionView(String questionId, int points){
        this.questionId = questionId;
        setPoints(points);
    }



    //Getters/Setters//
    public String getExamId() {
        return examId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public int getPoints() {
        return points;
    }



    void setExam(String examId) {
        this.examId = examId;
    }

    public void setPoints(int points) {
        if(points <= 0){
            throw new IllegalArgumentException("Points must be positive");
        }
        this.points = points;
    }

}
