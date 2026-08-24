package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;

import java.io.IOException;

public class TeacherStatisticsClientLogic {
    private final SimpleClient client;

    public TeacherStatisticsClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestTeacherStatistics() {
        try {
            client.sendToServer(new HSTSMessage(MessageType.GET_TEACHER_STATISTICS_REQUEST, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
