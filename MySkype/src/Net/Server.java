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
    }

    @Override
    public void run() {
        while (run) {
            System.out.println("Server: Waiting for peers.");
            try {
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                System.out.println("Server: Peer accepted");
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