package il.cshaifasweng.hsts.client.screens;

import il.cshaifasweng.hsts.client.App;
import il.cshaifasweng.hsts.client.logic.LoginClientLogic;
import il.cshaifasweng.hsts.client.state.ClientSession;
import il.cshaifasweng.hsts.entities.HSTSMessage;
import il.cshaifasweng.hsts.entities.enums.MessageType;
import il.cshaifasweng.hsts.entities.view.CurrentSessionView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class LoginScreen {
    private LoginClientLogic loginClientLogic;


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;



    @FXML
    private void initialize() {
        loginClientLogic = new LoginClientLogic();
        EventBus.getDefault().register(this);
    }

    @FXML
    private void handleLogin(){
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if(username.isBlank()){
            statusLabel.setText("Username is required");
            return;
        }
        if(password.isBlank()){
            statusLabel.setText("Password is required");
            return;
        }
        loginButton.setDisable(true);
        statusLabel.setText("Logging in...");
        try {
            loginClientLogic.requestLogin(username, password);
        } catch (RuntimeException e) {
            statusLabel.setText("Could not contact the server");
            loginButton.setDisable(false);
        }
    }

    @Subscribe
    public void onHSTSMessage(HSTSMessage message) {
        Platform.runLater(()->{
            MessageType type = message.getType();
            switch (type){
                case LOGIN_RESPONSE:
                    CurrentSessionView currentSessionView = (CurrentSessionView) message.getPayload();
                    statusLabel.setText("Welcome, " + currentSessionView.getFullName());
                    ClientSession.setCurrentSession(currentSessionView);
                    try {
                        App.showMainMenu();
                        shutdown();
                    }
                    catch (IOException e) {
                        statusLabel.setText("Could not open the main menu");
                    }
                    break;

                case ERROR:
                    statusLabel.setText(String.valueOf(message.getPayload()));
                    passwordField.clear();
                    loginButton.setDisable(false);
                    break;

                default:
                    break;
            }
        });
    }

    public void shutdown() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
