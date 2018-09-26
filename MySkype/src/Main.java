
import Net.ServerHandler;
import Handler.StandardInputHandler;
import Handler.StateHandler;
import Net.Client;
import Net.Server;
import States.State;
import States.StateReady;

import java.io.IOException;

public class Main {

    private static Server server    = null;
    private static Client client    = null;

    private static boolean run      = true;


    /*
    public static class ReadyState extends PhoneState  {

        private boolean callingSelf(String name, int port) throws IOException {
            return (name.equals("localhost") || InetAddress.getLocalHost().getHostAddress().equals(name))
                    && server.getPort() == port;
        }

        @Override
        public synchronized void call(String name, int port) {
            try {
                if (callingSelf(name, port)) {
                    System.err.println(Strings.SELF_CALL_ERR);
                    return;
                }
                System.out.println("calling: " + name + ":" + port);
                client = new Net.Client(name, port);
                client.write(Net.Protocol.INVITE);
                client.start();
                setState(new CallingState());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        @Override
        public synchronized void ring() {
            System.out.println("Someone is ringing...");
            server.write(Net.Protocol.TRO);
            setState(new RingingState());
        }
    }



    public static class CallingState extends PhoneState  {

        @Override
        public synchronized void acknowledge(String arg) {
            if (arg.equals(Net.Protocol.TRO)) {
                System.out.println("TRO ACK sent");
                client.write(Net.Protocol.TRO_ACK);
            }
            else {
                System.out.println("TROACK FAILED");
            }
        }

        @Override
        public synchronized void hangup() {
            System.out.println("Aborting call...");
            setState(new ReadyState());
        }
    }


    public static class RingingState extends PhoneState  {

        @Override
        public synchronized void acknowledge(String arg) {
            if (arg.equals(Net.Protocol.TRO_ACK)) {

            }
        }

        @Override
        public synchronized void hangup() {
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
        public synchronized void hangup() {
            System.out.println("Hanging up...");
            setState(new ReadyState());
        }
    }
    */

    public static void waitForQuitSignal(StateHandler state) {
        while (!state.shouldQuit()) {
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        State state = new StateReady();
        try {
            state.server = new Server(Integer.parseInt(args[0]));
            state.server.start();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.err.println(Strings.SERVER_ARG_ERR + " " + Strings.APP_TERM);
        } catch (IOException ioe) {
            System.err.println(Strings.SERVER_START_ERR + " " + Strings.APP_TERM);
        }

        StandardInputHandler userInput = new StandardInputHandler();
        userInput.start();
        StateHandler stateHandler = StateHandler.getInstance();
        stateHandler.setState(state);

        waitForQuitSignal(stateHandler);

        System.out.println("Program terminated.");

        userInput.close();

    }
}
