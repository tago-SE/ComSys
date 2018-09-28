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
    }

    // Do not synchronize this method will cause deadlock when a remote host is closed.
    public void drop() {
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
        System.out.println("Server dropped client.");
    }

    public void write(String msg) throws IOException {
        System.out.println("Server w/ " + msg);
        if (out != null)
            out.println(msg);
        else {
            throw new IOException();
        }
    }

    private synchronized boolean handleIncomingConnections(Socket socket ) throws IOException {
        if (clientSocket != null) {
            return false;
        }
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        //lås upp mutex
        return true;
    }

    // Using synchronized here causes deadlock ???
    public synchronized boolean hasConnection() {
        return clientSocket != null;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Socket socket = socket = serverSocket.accept();
                if (!handleIncomingConnections(socket)) {
                    System.out.println("Server: Peer rejected");
                    socket.close();
                    continue;
                } else {
                    System.out.println("Server: Peer accepted");
                }
            } catch (IOException e) {
                System.err.println("Server: " + e.getMessage());
                run = false;
            }
            while (run) {
                try {
                    String line = in.readLine();
                    System.out.println("Server r/ " + line);
                    if (line != null)
                        stateHandler.parseProtocolDataUnit(line);
                } catch (IOException | NullPointerException e) {
                    System.err.println("Server: " + e.getMessage());
                    stateHandler.error();
                    break;
                }
            }
        }
        System.out.println("Server stopped.");
    }
}