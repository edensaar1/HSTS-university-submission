package il.cshaifasweng.hsts.client.ocsf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractClient implements Runnable {
    private String host;
    private int port;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Thread clientReader;
    private boolean readyToStop;

    protected AbstractClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public final void openConnection() throws IOException {
        if (isConnected()) {
            return;
        }

        clientSocket = new Socket(host, port);
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        input = new ObjectInputStream(clientSocket.getInputStream());
        readyToStop = false;
        clientReader = new Thread(this);
        clientReader.start();
        connectionEstablished();
    }

    public void sendToServer(Object message) throws IOException {
        if (!isConnected()) {
            throw new IOException("Client is not connected.");
        }

        output.writeObject(message);
        output.flush();
    }

    public final void closeConnection() throws IOException {
        readyToStop = true;

        if (input != null) {
            input.close();
        }
        if (output != null) {
            output.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }

        connectionClosed();
    }

    public final boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public final String getHost() {
        return host;
    }

    public final int getPort() {
        return port;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final void setHost(String host) {
        this.host = host;
    }

    public final InetAddress getInetAddress() {
        return clientSocket.getInetAddress();
    }

    @Override
    public final void run() {
        try {
            while (!readyToStop) {
                Object message = input.readObject();
                handleMessageFromServer(message);
            }
        } catch (Exception exception) {
            if (!readyToStop) {
                connectionException(exception);
            }
        } finally {
            try {
                if (isConnected()) {
                    closeConnection();
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected abstract void handleMessageFromServer(Object message);

    protected void connectionEstablished() {
    }

    protected void connectionClosed() {
    }

    protected void connectionException(Exception exception) {
    }
}
