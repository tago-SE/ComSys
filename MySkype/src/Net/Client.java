package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client implements Closeable {

    public final int port;
    public final Socket socket;
    public final PrintWriter out;
    public final BufferedReader in;
    public boolean run;
    private final  StateHandler stateHandler = StateHandler.getInstance();
    private Thread thread;

    public Client(String name, int port) throws IOException {
        System.out.println("Client connecting/ " + name + ":" + port);
        this.port = port;


        socket = new Socket(name, port);



        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connection established");
        run = true;
        (thread = new Thread(()-> {
            while (run) {
                try {
                    String line = in.readLine();
                    System.out.println("Client r/ " + line);
                    stateHandler.parseProtocolDataUnit(line);
                } catch (IOException | NullPointerException e) {
                    close();
                    stateHandler.error();
                }
            }
        })).start();
    }

    public void write(String msg) throws IOException {
        System.out.println("Client w/ " + msg);
        try {
            out.println(msg);
        } catch (Exception e) {
            close();
            throw new IOException();    // Not sure if used/needed
        }
    }

    public synchronized void setTimeout(int time) throws SocketException {
        socket.setSoTimeout(time);
    }

    @Override
    public void close() {
        thread.interrupt();
        run = false;
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client closed.");
    }
}