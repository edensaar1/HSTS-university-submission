package il.cshaifasweng.hsts.entities.request;

import il.cshaifasweng.hsts.entities.view.SubmissionAnswerView;

import java.io.Serializable;
import java.util.List;

public class SubmitExamRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long submissionId;
    private final List<SubmissionAnswerView> answers;

    public SubmitExamRequest(Long submissionId, List<SubmissionAnswerView> answers) {
        this.submissionId = submissionId;
        this.answers = answers;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public List<SubmissionAnswerView> getAnswers() {
        return answers;
    }
}
