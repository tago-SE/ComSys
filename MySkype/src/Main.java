import Phone.PhoneState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Main {

    private static Server server    = null;
    private static Client client    = null;

    private static boolean run      = true;

    public static class ReadyState extends PhoneState  {

        private boolean callingSelf(String name, int port) throws IOException {
            return (name.equals("localhost") || InetAddress.getLocalHost().getHostAddress().equals(name))
                    && server.getPort() == port;
        }

        @Override
        public void call(String name, int port) {
            try {
                if (callingSelf(name, port)) {
                    System.err.println("Cannot call self.");
                    return;
                }
                System.out.println("calling: " + name + ":" + port);
                client = new Client(name, port);
                client.write(Protocol.INVITE);
                setState(new CallingState());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public void ring() {
            System.out.println("Someone is ringing...");
            // Server response TRO
            setState(new RingingState());
        }
    }



    public static class CallingState extends PhoneState  {

        // Waiting for TRO to be received on the server

        @Override
        public void hangup() {
            System.out.println("Aborting call...");
            setState(new ReadyState());
        }
    }


    public static class RingingState extends PhoneState  {

        // Waiting for TRO-ACK to be received on the client

        @Override
        public void hangup() {
            System.out.println("hanging up...");
            setState(new ReadyState());
        }

        @Override
        public void answer() {
            System.out.println("Answering...");
            // Acknowledge
            setState(new SpeakingState());
        }
    }

    public static class SpeakingState extends PhoneState  {
        @Override
        public void hangup() {
            System.out.println("Hanging up...");
            setState(new ReadyState());
        }
    }

    public class WaitingState extends PhoneState {
        // Wait for specific messages from server/client
        // if you are calling
    }

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
                    PhoneState.instance.call(args[1], Integer.parseInt(args[2]));
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
        PhoneState.setState(new ReadyState());
        try {
            server = new Server(Integer.parseInt(args[0]));
            server.start();
            (userInput = standardInputHandler()).start();

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(Strings.SERVER_ARG_ERR + " " + Strings.APP_TERM);
        } catch (IOException io) {
            System.err.println(Strings.SERVER_START_ERR + " " + Strings.APP_TERM);
        }
    }
}
