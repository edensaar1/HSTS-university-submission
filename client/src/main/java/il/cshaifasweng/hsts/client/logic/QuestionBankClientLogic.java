package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.view.TeacherQuestionView;

import java.io.IOException;


public class QuestionBankClientLogic {
    private final SimpleClient client;

    public QuestionBankClientLogic() {
        client = SimpleClient.getClient();
    }





    public void requestTeacherCourses() {
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.GET_TEACHER_COURSES_REQUEST, null);
        try {
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestCourseQuestions(String courseId) {
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.GET_COURSE_QUESTIONS_REQUEST, courseId);
        try {
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestCreateQuestion(TeacherQuestionView question) {
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.CREATE_QUESTION_REQUEST, question);
        try {
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestUpdateQuestion(TeacherQuestionView question) {
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.UPDATE_QUESTION_REQUEST, question);
        try {
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestDeleteQuestion(String questionId) {
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.DELETE_QUESTION_REQUEST, questionId);
        try {
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}

