package il.cshaifasweng.hsts.client.network;

import il.cshaifasweng.hsts.client.ocsf.AbstractClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import org.greenrobot.eventbus.EventBus;

public class SimpleClient extends AbstractClient {
    private static SimpleClient client = null;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    public static SimpleClient getClient(){
        if (client == null) {
            String host = System.getenv("HSTS_SERVER_HOST");
            if(host == null || host.isBlank()){
                host = "localhost";
            }
            client = new SimpleClient(host, 3000);
        }
        return client;
    }




    @Override
    protected void handleMessageFromServer(Object message) {
        if(!(message instanceof HSTSMessage)){
            return;
        }

        HSTSMessage hstsMessage = (HSTSMessage) message;
        EventBus.getDefault().post(hstsMessage);
    }
}
