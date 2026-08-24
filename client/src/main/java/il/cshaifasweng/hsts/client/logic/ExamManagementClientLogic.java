package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.ExtendExamDurationRequest;
import il.cshaifasweng.hsts.entities.view.ExamInstanceView;

import java.io.IOException;

public class ExamManagementClientLogic {
    private final SimpleClient client;

    public ExamManagementClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestApprovedExams() {
        send(new HSTSMessage(MessageType.GET_APPROVED_EXAMS_REQUEST, null));
    }

    public void scheduleExam(ExamInstanceView instanceView) {
        send(new HSTSMessage(MessageType.SCHEDULE_EXAM_REQUEST, instanceView));
    }

    public void requestTeacherExamInstances() {
        send(new HSTSMessage(MessageType.GET_TEACHER_EXAM_INSTANCES_REQUEST, null));
    }

    public void extendExamDuration(Long instanceId, int additionalMinutes) {
        ExtendExamDurationRequest request = new ExtendExamDurationRequest(instanceId, additionalMinutes);
        send(new HSTSMessage(MessageType.EXTEND_EXAM_DURATION_REQUEST, request));
    }

    private void send(HSTSMessage message) {
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
