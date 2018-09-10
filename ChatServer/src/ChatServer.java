import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread {

    private static final int DEFAULT_PORT = 9595;
    private static ServerSocket serverSocket = null;
    private static final String DEFAULT_NICKNAME = "Unknown";

    public class Client {
        String nickname = DEFAULT_NICKNAME;
        Socket socket = null;
    }

    /*
    When a client connects, the server replies with a welcome message to the client
    */
    /*
    When a client sends a text message to the server, the server sends the client name and the message to all
    other connected clients. This part is asynchronous, i.e. a client can receive a message from the server at any time.
    A message starting with / is considered to be a command. The server supports the following client commands
    */
    /*
    Commands
    /quit
    /who
    /nickname <nickname>
    /help
    / <response: invalid command
     */

    private void broadcast(String msg) {

    }

    public void run() {
        // Starts a new server socket on first thread
        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(DEFAULT_PORT);
                System.out.println("Chat server started...");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        Client client = new Client();
        try {
            System.out.println("Waiting for client...");
            client.socket = serverSocket.accept();
            new ChatServer().start();
        } catch (IOException e) {
            System.err.println("Failed to accept client.");
            new ChatServer().start();
            return;
        }
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            out = new PrintWriter(client.socket.getOutputStream(), true);

            String received;
            while ((received = in.readLine()) != null) {
                System.out.println("Received: " + received);
                out.println("Echo: " + received);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (client.socket != null)
                    client.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
