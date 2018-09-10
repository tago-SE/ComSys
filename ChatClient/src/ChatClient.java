import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private static Socket socket = null;
    private static BufferedReader in = null;
    private static BufferedReader userInput = null;
    private static PrintWriter out = null;
    private static String serverAddress;
    private static int port;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid argument.");
            System.exit(1);
        }
        serverAddress = args[0];
        port = Integer.parseInt(args[1]);
        socket = null;
        in = null;
        out = null;
        try {

            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));

            do {
                System.out.print ("input: ");
                out.println(userInput.readLine());
                System.out.println("echo: " + in.readLine());
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
                if (userInput != null)
                    userInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
