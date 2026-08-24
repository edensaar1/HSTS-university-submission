package il.cshaifasweng.hsts.entities;

import javax.persistence.*;

@Entity
@Table(name = "exam_questions")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private int points;

    protected ExamQuestion(){
    }

    public ExamQuestion(Question question, int points){
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        this.question = question;
        setPoints(points);
    }


//Getters/Setters//
    public int getId() {
        return id;
    }

    public Exam getExam() {
        return exam;
    }

    public Question getQuestion() {
        return question;
    }

    public int getPoints() {
        return points;
    }



    void setExam(Exam exam) {
        this.exam = exam;
    }

    public void setPoints(int points) {
        if(points <= 0){
            throw new IllegalArgumentException("Points must be positive");
        }
        this.points = points;
    }

}
