package Net;

import Handler.StateHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
    public void run() {
        try {
            run = true;
            while (run) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                System.out.println("Client r/ " + line);
                stateHandler.parseProtocolDataUnit(line);
            }
        } catch (IOException | NullPointerException e) {
            if (e instanceof SocketException || e instanceof SocketTimeoutException)
                System.err.println(e.getMessage());
            else e.printStackTrace();
        } finally {
            if (run) close();
        }
    }

    public synchronized void write(String msg) throws IOException {
        System.out.println("Client w/ " + msg);
        try {
            out.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            socket.close();
            out.close();
            in.close();
            System.out.println("Client closed!");
            stateHandler.setClient(null);
            stateHandler.error();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            socket  = null;
            out = null;
            in = null;
        }
    }
}