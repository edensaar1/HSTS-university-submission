package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.StartExamRequest;
import il.cshaifasweng.hsts.entities.request.SubmitExamRequest;
import il.cshaifasweng.hsts.entities.view.SubmissionAnswerView;

import java.io.IOException;
import java.util.List;

public class ExamExecutionClientLogic {
    private final SimpleClient client;

    public ExamExecutionClientLogic() {
        client = SimpleClient.getClient();
    }

    public void startExam(String executionCode, String studentId) {
        StartExamRequest request = new StartExamRequest(executionCode, studentId);
        send(new HSTSMessage(MessageType.START_EXAM_REQUEST, request));
    }

    public void submitExam(Long submissionId, List<SubmissionAnswerView> answers) {
        SubmitExamRequest request = new SubmitExamRequest(submissionId, answers);
        send(new HSTSMessage(MessageType.SUBMIT_EXAM_REQUEST, request));
    }

    public void timeoutExam(Long submissionId, List<SubmissionAnswerView> answers) {
        SubmitExamRequest request = new SubmitExamRequest(submissionId, answers);
        send(new HSTSMessage(MessageType.TIMEOUT_EXAM_REQUEST, request));
    }

    private void send(HSTSMessage message) {
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
