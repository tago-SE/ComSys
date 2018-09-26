package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.Socket;

public class Client  extends Thread implements Closeable {

    public final int port;
    public final Socket socket;
    public final PrintWriter out;
    public final BufferedReader in;
    public boolean run;
    private final  StateHandler stateHandler = StateHandler.getInstance();

    public Client(String name, int port) throws IOException {
        System.out.println("Client connecting/ " + name + ":" + port);
        this.port = port;
        socket = new Socket(name, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connection established");
        run = true;
    }

    public void write(String msg) throws IOException {
        System.out.println("Client w/ " + msg);
        if (out != null)
            out.println(msg);
        else throw new IOException();
    }

    @Override
    public void run() {
        while (run) {
            try {
                if (in.ready()) {
                    String line = in.readLine();
                    System.out.println("Client r/ " + line);
                    stateHandler.parseProtocolDataUnit(line);
                }
            } catch (IOException | NullPointerException e) {
                run = false;
                System.out.println("Client read error");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        interrupt();
        run = false;
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}