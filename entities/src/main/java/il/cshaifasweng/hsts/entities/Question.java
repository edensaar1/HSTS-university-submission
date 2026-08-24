package il.cshaifasweng.hsts.entities;

import javax.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Id
    @Column(name = "question_id", length = 5)
    private String questionId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String answer1;

    @Column(nullable = false)
    private String answer2;

    @Column(nullable = false)
    private String answer3;

    @Column(nullable = false)
    private String answer4;

    @Column(name = "correct_answer", nullable = false)
    private int correctAnswer;

    private String illustrationPath;

    protected Question(){
    }

    public Question(Course course, String questionId, String description, String answer1, String answer2, String answer3,
                      String answer4, int correctAnswer,
                      String illustrationPath){
        this.course = course;
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

    public Course getCourse() {
        return course;
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
