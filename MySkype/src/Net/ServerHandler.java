package Net;

import Handler.StateHandler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class ServerHandler extends Thread implements Closeable {

    private StateHandler stateHandler;
    private BufferedReader in ;
    private PrintWriter out;
    private ServerSocket serverSocket;
    private boolean run;

    public ServerHandler(StateHandler stateHandler, int port) throws IOException {
        this.stateHandler = stateHandler;
        run = true;
        stateHandler.getState().serverPort = port;
        stateHandler.getState().serverSocket  = new ServerSocket(port);
        serverStartedMessage(port);
    }

    private void serverStartedMessage(int port) throws IOException{
        Socket socket = null;
        try {
            (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));
            System.out.println("Net.Server started on: " + socket.getLocalAddress() + ":" + port);
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
        interrupt();
    }
}
