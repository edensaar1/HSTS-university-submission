package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.ApproveSubmissionRequest;

import java.io.IOException;

public class ExamSubmissionManagementClientLogic {
    private final SimpleClient client;

    public ExamSubmissionManagementClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestSubmissionsForReview() {
        send(new HSTSMessage(MessageType.GET_SUBMISSIONS_FOR_REVIEW_REQUEST, null));
    }

    public void approveSubmission(Long submissionId, int finalGrade, String teacherComment,
                                  String gradeChangeReason) {
        ApproveSubmissionRequest request = new ApproveSubmissionRequest(
                submissionId, finalGrade, teacherComment, gradeChangeReason);
        send(new HSTSMessage(MessageType.APPROVE_SUBMISSION_REQUEST, request));
    }

    private void send(HSTSMessage message) {
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
