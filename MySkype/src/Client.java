import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String name, int port) throws IOException {
        System.out.println("Client connecting/ " + name + ":" + port);
        socket = new Socket(name, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Connection established");

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

    public void close() {
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}