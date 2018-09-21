import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class MySkype {

    //private static Peer peer        = null;
    private static Server server    = null;
    private static boolean run      = true;
    private static State state;

    public static class ReadyState extends State  {
        @Override
        public void call(String name, int port) {
            System.out.println("calling..." + name + " on " + port);
            super.state = new CallingState();
        }

        @Override
        public void ring() {
            super.state = new RingingState();
        }
    }

    public static class CallingState extends State  {
        @Override
        public void hangup() {
            System.out.println("Aborting call...");
            super.state = new ReadyState();
        }
    }

    public static class RingingState extends State  {
        public RingingState() {
            new Thread(() -> {
                try {
                    Thread.sleep(250);
                    if (super.state instanceof RingingState)
                        System.out.println("Ringing...");
                    else return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        @Override
        public void hangup() {
            System.out.println("hanging up...");
            super.state = new ReadyState();
        }

        @Override
        public void answer() {
            System.out.println("Answering...");
            super.state = new SpeakingState();
        }
    }

    public static class SpeakingState extends State  {
        @Override
        public void hangup() {
            System.out.println("Hanging up...");
            super.state = new ReadyState();
        }
    }

    public class WaitingState extends State  {
        // Wait for specific messages from server/client
        // if you are calling
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
                System.out.println(Strings.SERVER_STARTED_ON + socket.getLocalAddress() + ":" + this.port);
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
    /*
    public static class Peer {
        private Socket socket;

        public void call(String name, int port) {
            System.out.println("calling..." + name + " on " + port);

            // On Success start a thread for managing the call
        }

        public void hangup() {  // interface shared by server and client ?

        }

        public void answer() {  // interface shared by server and client ?

        }

    }
    */

    private static void handleCommands(String[] args) {
        if (args == null || args.length <= 0)
            return;
        switch (args[0]) {
            case Strings.CMD_QUIT: {
                System.out.println("Quitting...");
                run = false;
            } break;
            case Strings.CMD_CALL: {
                try {
                    //peer.call(args[1], Integer.parseInt(args[2]));

                    state.call(args[1], Integer.parseInt(args[2]));



                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {    // Expected failures
                    System.err.println(Strings.CMD_INVALID_CALL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // set state to busy
                // Errors
                // 1) You are busy (in another call)
                // 2) The other peer is busy
                // 3) Failed to establish connection
                // 4) Attempted to call yourself
            } break;
            case Strings.CMD_HANGUP: {
                System.out.println("Hanging up...");
                // Errros
                // 1) You are not busy (in another call)
            } break;
            case Strings.CMD_ANSWER: {
                // set state to busy (need to be mutex)
                System.out.println("Answering...");
                // Errors
                // 1) There is no pending call
            } break;
            default: {
                System.err.println(Strings.CMD_INVALID);
            }
        }
    }

    private static Thread standardInputHandler() {
        return new Thread(() -> {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            while (run) {
                try {
                    if (stdin.ready()) {
                        String line = stdin.readLine();
                        if (line.length() == 0)
                            continue;
                        if (line.charAt(0) == '/')
                            handleCommands(line.split(" "));
                    }
                } catch (IOException e) {
                    System.err.println(Strings.STDIO_ERR);
                    run = false;
                }
            }
            try {
                stdin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Standard I/O closing...");
        });
    }

    public static void main(String[] args) {
        Thread userInput = null;

        state = new ReadyState();

        try {
            //peer = new Peer();
            (server = new Server(Integer.parseInt(args[0]))).start();
            (userInput = standardInputHandler()).start();

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(Strings.SERVER_ARG_ERR + " " + Strings.APP_TERM);
        } catch (IOException io) {
            System.err.println(Strings.SERVER_START_ERR + " " + Strings.APP_TERM);
        }
    }
}
