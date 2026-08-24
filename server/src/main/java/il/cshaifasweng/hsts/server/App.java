package il.cshaifasweng.hsts.server;
import il.cshaifasweng.hsts.server.config.DatabaseManager;
import il.cshaifasweng.hsts.server.network.SimpleServer;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try{
            DatabaseManager.initialize();
            SimpleServer server = new SimpleServer(3000);
            server.listen();

            System.out.println("HSTS server listening on port " + server.getPort());

        }
        catch (Exception e) {
            System.err.println("Could not start HSTS server");
            e.printStackTrace();
            DatabaseManager.close();
        }
    }
}
