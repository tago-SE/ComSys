import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MySkype {

    private static Server server = null;
    private static UserInput ui = null;
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
                System.out.println(Strings.SERVER_STARTED_ON + socket.getLocalAddress() + ": " + this.port);
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
                System.out.println(Strings.SERVER_WAITING_FOR_CLIENT);
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
                    System.err.println(Strings.SERVER_ACCEPT_ERR);
                }
            }
        }
    }

    /* contains a thread listening for messages from the server */
    public static class Peer {
        // has a reader thread

        public void call(String name, int port) {

        }

        public void hangup() {  // interface shared by server and client ?

        }

        public void answer() {  // interface shared by server and client ?

        }

    }

    public static class UserInput extends Thread {
        public void run() {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            int callLength = Strings.CMD_CALL.length();
            while (run) {
                try {
                    if (stdin.ready()) {
                        String line = stdin.readLine();
                        if (line.length() == 0) {
                            continue;
                        }
                        if (line.charAt(0) == '/') {
                            if (line.equals(Strings.CMD_QUIT)) {
                                System.out.println("Quitting...");
                                run = false;
                            }
                            else if (line.length() >= callLength && line.substring(0, callLength).equals(Strings.CMD_CALL)) {
                                // set state to busy
                                System.out.println("calling...");
                                // Errors
                                // 1) You are busy (in another call)
                                // 2) The other peer is busy
                                // 3) Failed to establish connection
                                // 4) Attempted to call yourself
                            }
                            else if (line.equals(Strings.CMD_HANGUP)) {
                                System.out.println("Hanging up...");
                                // Errros
                                // 1) You are not busy (in another call)
                            }
                            else if (line.equals(Strings.CMD_ANSWER)) {
                                // set state to busy (need to be mutex)
                                System.out.println("Answering...");
                                // Errors
                                // 1) There is no pending call
                            }
                            else {
                                System.err.println(Strings.CMD_INVALID);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println(Strings.STDIO_ERR);
                    e.printStackTrace();
                    run = false;
                }
            }
            try {
                stdin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            (server = new Server(Integer.parseInt(args[0]))).start();
            (ui = new UserInput()).start();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(Strings.SERVER_ARG_ERR + " " + Strings.APP_TERM);
        } catch (IOException io) {
            System.err.println(Strings.SERVER_START_ERR + " " + Strings.APP_TERM);
        }
    }
}
