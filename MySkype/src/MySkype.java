import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;

public class MySkype {

    private static final String SERVER_ARG_ERR = "Server: Invalid argument.";
    private static final String SERVER_START_ERR = "Server: Failed to start.";
    private static final String SERVER_STARTED_ON = "Server: Started";
    private static final String SERVER_WAITING_FOR_CLIENT = "Server: Waiting for peer...";
    private static final String SERVER_ACCEPT_ERR = "Server: Failed to accept peer.";
    private static final String APP_TERM = "Application terminated.";
    private static final String CMD_QUIT        = "/quit";
    private static final String CMD_CALL        = "/call";
    private static final String CMD_HANGUP      = "/hangup";
    private static final String CMD_ANSWER      = "/answer";

    private static Server server = null;
    private static BufferedReader userInput = null;
    private static boolean run = true;


    public enum State {
        Waiting, Calliong, Speaking, Hangingup;

        public boolean isBusy() {
            return this != State.Waiting;
        }
    }


    /* Server class contains a thread for listening to messages sent from another peer (client). */
    public static class Server extends Thread {
        private int port;
        private ServerSocket serverSocket = null;
        private Socket clientSocket = null;

        private void serverStartedMessage() throws IOException{
            Socket socket = null;
            try {
                (socket = new Socket()).connect(new InetSocketAddress("google.com", 80));
                System.out.println(SERVER_STARTED_ON + socket.getLocalAddress() + ": " + this.port);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null)
                    socket.close();
            }
        }

        public Server(int port) throws IOException {
            this.port = port;
            serverSocket = new ServerSocket(port);
            serverStartedMessage();
        }

        @Override
        public void run() {
            while (run) {
                System.out.println(SERVER_WAITING_FOR_CLIENT);
                try {
                    Socket socket = serverSocket.accept();
                    if (clientSocket == null) { // or state is busy (calling, talking, hanging up)
                        // accept client
                        clientSocket = socket;
                        // State: Waiting for <Invite>
                    }
                    else {
                        // Send busy to socket
                    }
                } catch (IOException e) {
                    System.err.println(SERVER_ACCEPT_ERR);
                }
            }
        }
    }

    /* contains a thread listening for messages from the server */
    public static class Peer {
        // has a reader thread

        public void connect(String name, int port) {

        }
    }




        /*
        private ServerSocket serverSocket = null;
        private Socket clientSocket = null;
        private BufferedReader in = null;
        private BufferedReader userInput = null;
        private PrintWriter out = null;
        private String serverAddress;
        private int port;
        private boolean run;

        public class Reader extends Thread {
            public void run() {
                while (true) {
                    try {
                        if (in == null) // temp bugfix
                            continue;
                        String line = in.readLine();
                        if (line != null) {
                            System.out.println(line);
                        }
                        else
                            throw new IOException("NullPointerException");
                    } catch (IOException e) {
                        System.err.println("Reader: " + e.getMessage());
                        run = false;
                        return;
                    }
                }
            }
        }

        public class Writer extends Thread {
            public void run() {
                while (true) {
                    try {
                        if (userInput.ready()) {
                            //out.println(userInput.readLine());
                            System.out.println("OUT:" + userInput.readLine());

                        }
                    } catch (IOException e) {
                        System.err.println("Writer: " + e.getMessage());
                        run = false;
                        return;
                    }
                }
            }
        }
    }

    */

    public static void main(String[] args) {
        try {
            server = new Server(Integer.parseInt(args[0]));
            userInput = new BufferedReader(new InputStreamReader(System.in));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(SERVER_ARG_ERR + " " + APP_TERM);
        } catch (IOException io) {
            System.err.println(SERVER_START_ERR + " " + APP_TERM);
        }

        int callLength = CMD_CALL.length();

        while (run = true) {
            try {
                if (userInput.ready()) {
                    String line = userInput.readLine();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.charAt(0) == '/') {
                        if (line.equals(CMD_QUIT)) {
                            System.out.println("Quitting...");
                            run = false;
                        }
                        else if (line.length() >= callLength && line.substring(0, callLength).equals(CMD_CALL)) {
                            // set state to busy
                            System.out.println("calling...");
                            // Errors
                            // 1) You are busy (in another call)
                            // 2) The other peer is busy
                            // 3) Failed to establish connection
                            // 4) Attempted to call yourself
                        }
                        else if (line.equals(CMD_HANGUP)) {
                            System.out.println("Hanging up...");
                            // Errros
                            // 1) You are not busy (in another call)
                        }
                        else if (line.equals(CMD_ANSWER)) {
                            // set state to busy (need to be mutex)
                            System.out.println("Answering...");
                            // Errors
                            // 1) There is no pending call
                        }
                        else {
                            // Currently ignored, can be used for testing...
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                run = false;
                return;
            } finally {
                // Don't forget to check for hanging threads (server etc)
            }
        }
    }
}
