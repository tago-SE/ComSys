package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread implements Closeable {

    private final StateHandler stateHandler;
    private ServerSocket serverSocket;
    private int port;

    private Client client = null;
    private boolean run;

    public Server(StateHandler handler, int port) {
        stateHandler = handler;
        try {
            serverSocket = new ServerSocket(port);
            this.port = port;
            serverStartedMessage(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    private void serverStartedMessage(int port) throws IOException{
        Socket socket = null;
        try {
            (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));
            System.out.println("Server started on: " + socket.getLocalAddress() + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private synchronized boolean handleIncomingConnections(Socket socket ) throws IOException {
        if (client != null){
            return false;
        }
        client = new Client(stateHandler);
        client.connect(socket);
        return true;
    }


    @Override
    public void run() {
        run = true;
        try {
            while (run) {
                Socket socket = serverSocket.accept();
                if (!handleIncomingConnections(socket)) {
                    System.out.println("Server: Peer rejected");
                    socket.close();
                    continue;
                } else {
                    System.out.println("Server: Peer accepted");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    @Override
    public void close() throws IOException {
        run = false;
        serverSocket.close();
    }
}
