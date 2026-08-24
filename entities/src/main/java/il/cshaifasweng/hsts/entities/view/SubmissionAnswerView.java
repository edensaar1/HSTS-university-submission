package il.cshaifasweng.hsts.entities.view;

import java.io.Serializable;

public class SubmissionAnswerView implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int examQuestionId;
    private final int selectedAnswer;

    public SubmissionAnswerView(int examQuestionId, int selectedAnswer) {
        this.examQuestionId = examQuestionId;
        this.selectedAnswer = selectedAnswer;
    }

    public int getExamQuestionId() {
        return examQuestionId;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }
}
