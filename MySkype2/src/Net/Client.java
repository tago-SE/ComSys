package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client extends Thread implements Closeable {

    public int port;
    public Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    public boolean run;
    private final StateHandler stateHandler;

    public Client(StateHandler handler)  {
        this.stateHandler = handler;
    }

    public void connect(Socket socket) throws IOException {
        this.socket = socket;

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }

    public void connect(String name, int port) throws IOException {
        System.out.println("Client connecting/ " + name + ":" + port);
        this.port = port;
        socket = new Socket(name, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connection established");
        start();
    }

    @Override
    public synchronized void run() {
        try {
            run = true;
            while (run) {
                String line = in.readLine();
                if (line == null) break;

                //stateHandler.parseProtocolDataUnit(line);
                System.out.println("Client r/ " + line);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (run) close();
        }
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
        run = false;
        System.out.println("Client closed.");
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}