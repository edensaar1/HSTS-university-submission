package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class SubmissionReviewAnswerView implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examQuestionId;
    private String questionId;
    private String questionText;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int selectedAnswer;
    private int correctAnswer;
    private int points;

    public SubmissionReviewAnswerView(int examQuestionId, String questionId, String questionText, String answer1,
                                      String answer2, String answer3, String answer4, int selectedAnswer,
                                      int correctAnswer, int points) {
        this.examQuestionId = examQuestionId;
        this.questionId = questionId;
        this.questionText = questionText;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    public int getExamQuestionId() {
        return examQuestionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
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

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public int getPoints() {
        return points;
    }
}
