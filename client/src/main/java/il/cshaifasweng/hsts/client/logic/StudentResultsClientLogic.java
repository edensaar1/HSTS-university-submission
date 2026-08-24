package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;

import java.io.IOException;

public class StudentResultsClientLogic {
    private final SimpleClient client;

    public StudentResultsClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestStudentResults() {
        try {
            client.sendToServer(new HSTSMessage(MessageType.GET_STUDENT_RESULTS_REQUEST, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
