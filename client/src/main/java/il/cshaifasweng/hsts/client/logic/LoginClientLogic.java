package il.cshaifasweng.hsts.client.logic;

import il.cshaifasweng.hsts.client.network.SimpleClient;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.request.LoginRequest;

import java.io.IOException;

public class LoginClientLogic {
    private final SimpleClient client;

    public LoginClientLogic() {
        client = SimpleClient.getClient();
    }

    public void requestLogin(String username, String password){
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.LOGIN_REQUEST, new LoginRequest(username, password));
        try{
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestLogout(){
        HSTSMessage hstsMessage = new HSTSMessage(MessageType.LOGOUT_REQUEST, null);
        try{
            client.sendToServer(hstsMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
