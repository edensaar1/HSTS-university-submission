package il.cshaifasweng.hsts.entities.request;

import java.io.Serializable;

public class RejectExamRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String examId;
    private final String reason;

    public RejectExamRequest(String examId, String reason) {
        this.examId = examId;
        this.reason = reason;
    }

    public String getExamId() {
        return examId;
    }

    public String getReason() {
        return reason;
    }
}
