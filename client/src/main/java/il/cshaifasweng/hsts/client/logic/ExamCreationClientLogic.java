package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.view.ExamView;

import java.io.IOException;

public class ExamCreationClientLogic {
    private final SimpleClient client;

    public ExamCreationClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestTeacherCourses() {
        HSTSMessage message = new HSTSMessage(
                MessageType.GET_TEACHER_COURSES_REQUEST, null);
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestCourseQuestions(String courseId) {
        HSTSMessage message = new HSTSMessage(
                MessageType.GET_COURSE_QUESTIONS_REQUEST, courseId);
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestCreateExam(ExamView examView) {
        HSTSMessage message = new HSTSMessage(MessageType.CREATE_EXAM_REQUEST, examView);
        try {
            client.sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
