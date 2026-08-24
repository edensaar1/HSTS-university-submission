package il.cshaifasweng.hsts.server.ocsf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractServer implements Runnable {
    private int port;
    private int timeout;
    private int backlog;
    private final ThreadGroup clientThreadGroup;
    private final List<ConnectionToClient> clientConnections;
    private AbstractConnectionFactory connectionFactory;
    private ServerSocket serverSocket;
    private Thread listener;
    private boolean readyToStop;

    protected AbstractServer(int port) {
        this(port, new AbstractConnectionFactory());
    }

    protected AbstractServer(int port, AbstractConnectionFactory connectionFactory) {
        this.port = port;
        timeout = 500;
        backlog = 10;
        this.connectionFactory = connectionFactory;
        clientThreadGroup = new ThreadGroup("client-connections");
        clientConnections = Collections.synchronizedList(new ArrayList<>());
    }

    public final void listen() throws IOException {
        if (isListening()) {
            return;
        }

        serverSocket = new ServerSocket(port, backlog);
        serverSocket.setSoTimeout(timeout);
        readyToStop = false;
        listener = new Thread(this);
        listener.start();
        serverStarted();
    }

    public final void stopListening() {
        readyToStop = true;
    }

    public final void close() throws IOException {
        readyToStop = true;

        if (serverSocket != null) {
            serverSocket.close();
        }

        synchronized (clientConnections) {
            for (ConnectionToClient client : clientConnections) {
                client.close();
            }
            clientConnections.clear();
        }

        serverStopped();
    }

    public final void sendToAllClients(Object message) throws IOException {
        synchronized (clientConnections) {
            for (ConnectionToClient client : clientConnections) {
                client.sendToClient(message);
            }
        }
    }

    public final boolean isListening() {
        return listener != null && listener.isAlive() && !readyToStop;
    }

    public final boolean isClosed() {
        return serverSocket == null || serverSocket.isClosed();
    }

    public final Thread[] getClientConnections() {
        synchronized (clientConnections) {
            return clientConnections.toArray(new Thread[0]);
        }
    }

    public final int getNumberOfClients() {
        return clientConnections.size();
    }

    public final int getPort() {
        return port;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public final void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public final void setConnectionFactory(AbstractConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public final void run() {
        try {
            while (!readyToStop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ConnectionToClient client = connectionFactory.createConnection(clientThreadGroup, clientSocket, this);
                    clientConnections.add(client);
                    client.start();
                    clientConnected(client);
                } catch (SocketTimeoutException ignored) {
                }
            }
        } catch (IOException exception) {
            if (!readyToStop) {
                listeningException(exception);
            }
        } finally {
            readyToStop = true;
        }
    }

    final void receiveMessageFromClient(Object message, ConnectionToClient client) {
        handleMessageFromClient(message, client);
    }

    final void clientDisconnectedInternal(ConnectionToClient client) {
        clientConnections.remove(client);
        clientDisconnected(client);
    }

    protected abstract void handleMessageFromClient(Object message, ConnectionToClient client);

    protected void serverStarted() {
    }

    protected void serverStopped() {
    }

    protected void listeningException(Exception exception) {
    }

    protected void clientConnected(ConnectionToClient client) {
    }

    protected void clientDisconnected(ConnectionToClient client) {
    }
}
