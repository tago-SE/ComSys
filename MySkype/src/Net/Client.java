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

    public void write(String msg)  {
        System.out.println("Client w/ " + msg);
        out.println(msg);
    }


    public String read() throws IOException {
        String msg = in.readLine();
        System.out.println("Client r/ " + msg);
        return msg;
    }


    @Override
    public void run() {
        while (run) {
            try {
                String msg = in.readLine();
                System.out.println("Client: " + msg);

                //PhoneState.instance.acknowledge(msg);
            } catch (IOException | NullPointerException e) {
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