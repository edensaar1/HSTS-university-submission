package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.RejectExamRequest;

import java.io.IOException;

public class ExamApprovalClientLogic {
    private final SimpleClient client;

    public ExamApprovalClientLogic(){
        client = SimpleClient.getClient();
    }

    public void requestPendingExams(){
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.GET_PENDING_EXAMS_REQUEST, null);
        try{
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void approveExam(String examId){
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.APPROVE_EXAM_REQUEST, examId);
        try{
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectExam(String examId, String reason){
        RejectExamRequest request = new RejectExamRequest(examId, reason);
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.REJECT_EXAM_REQUEST, request);
        try{
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
