package il.cshaifasweng.hsts.entities;

import javax.persistence.*;

@Entity
@Table(name = "submission_answers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"submission_id", "exam_question_id"})})
public class SubmissionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private ExamSubmission submission;

    @ManyToOne
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

    @Column(name = "selected_answer", nullable = false)
    private int selectedAnswer;

    protected SubmissionAnswer() {
    }

    public SubmissionAnswer(ExamQuestion examQuestion, int selectedAnswer) {
        setExamQuestion(examQuestion);
        setSelectedAnswer(selectedAnswer);
    }

    public Long getAnswerId() {
        return answerId;
    }

    public ExamSubmission getSubmission() {
        return submission;
    }

    public ExamQuestion getExamQuestion() {
        return examQuestion;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    void setSubmission(ExamSubmission submission) {
        this.submission = submission;
    }

    public void setExamQuestion(ExamQuestion examQuestion) {
        if (examQuestion == null) {
            throw new IllegalArgumentException("Exam question cannot be null");
        }
        this.examQuestion = examQuestion;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        if (selectedAnswer < 1 || selectedAnswer > 4) {
            throw new IllegalArgumentException("Selected answer must be between 1 and 4");
        }
        this.selectedAnswer = selectedAnswer;
    }
}
