package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server extends Thread implements Closeable {

    private final StateHandler stateHandler;
    private ServerSocket serverSocket;
    private int port;

    //private Client client = null;
    private boolean run;
    private Socket clientSocket = null;
    private BufferedReader in ;
    private PrintWriter out;



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
        if (clientSocket != null){
            return false;
        }
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        //client = new Client(stateHandler);
        //client.connect(socket);
        return true;
    }

    public synchronized void dropClient() {
        try {
            clientSocket.close();
            out.close();
            in.close();
            clientSocket = null;
            System.out.println("Server: Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setTimeout(int time) throws SocketException {
        clientSocket.setSoTimeout(time);
    }

    public void write(String msg) throws IOException {
        System.out.println("Server w/ " + msg);
        if (out != null)
            out.println(msg);
        else {
            throw new IOException();
        }
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
                    new Thread(()->{
                        while (true) {
                            try {
                                String line = in.readLine();
                                if (line == null) {
                                    dropClient();
                                    return;
                                }
                                System.out.println("Server r/" + line);
                                //stateHandler.parseProtocolDataUnit(line);
                            } catch (IOException e) {
                                if (e instanceof SocketException)
                                    System.err.println(e.getMessage());
                                else e.printStackTrace();
                                dropClient();
                                break;
                               // stateHandler.error();
                            }
                        }
                    }).start();

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
