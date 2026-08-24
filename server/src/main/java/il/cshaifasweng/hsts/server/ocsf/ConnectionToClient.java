package il.cshaifasweng.hsts.server.ocsf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ConnectionToClient extends Thread {
    private final Socket clientSocket;
    private final AbstractServer server;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final Map<String, Object> savedInfo;
    private boolean readyToStop;

    protected ConnectionToClient(ThreadGroup threadGroup, Socket clientSocket, AbstractServer server) throws IOException {
        super(threadGroup, "connection-to-client");
        this.clientSocket = clientSocket;
        this.server = server;
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        input = new ObjectInputStream(clientSocket.getInputStream());
        savedInfo = new HashMap<>();
    }

    public void sendToClient(Object message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    public final void close() throws IOException {
        readyToStop = true;
        input.close();
        output.close();
        clientSocket.close();
    }

    public final InetAddress getInetAddress() {
        return clientSocket.getInetAddress();
    }

    public final boolean isConnected() {
        return clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public final void setInfo(String infoType, Object info) {
        savedInfo.put(infoType, info);
    }

    public final Object getInfo(String infoType) {
        return savedInfo.get(infoType);
    }

    @Override
    public String toString() {
        return clientSocket.getInetAddress().getHostName() + " (" + clientSocket.getInetAddress().getHostAddress() + ")";
    }

    @Override
    public final void run() {
        try {
            while (!readyToStop) {
                Object message = input.readObject();
                handleMessageFromClient(message);
            }
        } catch (Exception ignored) {
        } finally {
            server.clientDisconnectedInternal(this);
            try {
                if (isConnected()) {
                    close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected boolean handleMessageFromClient(Object message) {
        server.receiveMessageFromClient(message, this);
        return true;
    }
}
