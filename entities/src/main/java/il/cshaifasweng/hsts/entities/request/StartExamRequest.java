package il.cshaifasweng.hsts.entities.request;

import java.io.Serializable;

public class StartExamRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String executionCode;
    private final String studentId;

    public StartExamRequest(String executionCode, String studentId) {
        this.executionCode = executionCode;
        this.studentId = studentId;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public String getStudentId() {
        return studentId;
    }
}
