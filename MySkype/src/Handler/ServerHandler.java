package Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler {

    private StateHandler stateHandler;
    private BufferedReader in ;
    private PrintWriter out;
    private int port;
    private ServerSocket serverSocket;
    private boolean run;

    public ServerHandler(StateHandler stateHandler, int port) throws IOException {
        System.out.println("What on earth...?");
        this.stateHandler = stateHandler;
        run = true;
        this.port = port;
        serverSocket = new ServerSocket(port);
        serverStartedMessage();
    }

    private void serverStartedMessage() throws IOException{
        Socket socket = null;
        try {
            (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));
            System.out.println("Server started on: " + socket.getLocalAddress() + ":" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }


}
