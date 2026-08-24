package il.cshaifasweng.hsts.server.ocsf;

import java.io.IOException;
import java.net.Socket;

public class AbstractConnectionFactory {
    public ConnectionToClient createConnection(ThreadGroup threadGroup, Socket clientSocket, AbstractServer server)
            throws IOException {
        return new ConnectionToClient(threadGroup, clientSocket, server);
    }
}
