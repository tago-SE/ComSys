package Net;

import Handler.StateHandler;
import Net.Protocol;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class Server extends Thread implements Closeable {

    public final int port;
    private final StateHandler stateHandler = StateHandler.getInstance();
    private final ServerSocket serverSocket;
    private boolean run;
    private Socket clientSocket;
    private BufferedReader in ;
    private PrintWriter out;

    public Server(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        run = true;
        serverStartedMessage(port);
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

    @Override
    public void close() throws IOException {
        run = false;
        // interrupt();
    }

    public synchronized void drop() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientSocket = null;
            out = null;
            in = null;
        }
    }

    public void write(String msg) throws IOException {
        System.out.println("Server w/ " + msg);
        if (out != null)
            out.println(msg);
        else {
            throw new IOException();
        }
    }

    private synchronized boolean handleIncomingConnections() throws IOException {
        Socket socket = socket = serverSocket.accept();
        if (clientSocket != null) {
            socket.close();
            return false;
        }
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        return true;
    }

    public synchronized boolean hasConnection() {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void run() {
        while (run) {

            try {
                System.out.println("Server: Waiting for peers.");
                if (!handleIncomingConnections()) {
                    System.out.println("Server: Peer rejected");
                    continue;
                } else {
                    System.out.println("Server: Peer accepted");
                }
                if (in.ready()) {
                    String line = in.readLine();
                    try {
                        stateHandler.parseProtocolDataUnit(line);
                    } catch (IllegalStateException e) {
                        System.out.println("Server: Illegal State Exception: " + StateHandler.getInstance());
                    }
                }
            } catch (IOException e) {
                run = false;
            }
        }
        drop();
        System.out.println("Server: Stopped.");
    }
}