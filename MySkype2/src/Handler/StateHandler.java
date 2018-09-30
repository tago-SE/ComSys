package Handler;

import Net.Client;
import Net.Server;
import State.State;

import java.io.IOException;

public class StateHandler {

    private static StateHandler ourInstance = new StateHandler();
    private State state;
    private boolean debugEnabled = false;

    private UserInput userInput;
    private Server server;
    private Client client;


    public static StateHandler getInstance() {
        return ourInstance;
    }

    private StateHandler() {
    }

    public final void startUserInput() {
        (userInput = new UserInput(this)).start();
    }

    public void startServer(int port) {
        (server = new Server(this, port)).start();
    }

    public void stop() {
        try {
            userInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public synchronized void setState(State state) {
        this.state = state;
    }

    public synchronized State getState() {
        return state;
    }

    private void parseDebugCommands(String[] args) {

    }

    private void toggleDebug() {
        debugEnabled = !debugEnabled;
        System.out.println("Debug = " + debugEnabled);
    }

    private synchronized void connect(String[] args) {
        client = new Client(this);
        try {
            String name = args[1];
            int port = Integer.parseInt(args[2]);
            if (port == server.getPort()) {
                System.err.println("Cannot connect to self.");
                return;
            }
            client.connect(name, port);
            System.out.println("Client connecting to: " + name + " on port " + port + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void disconnect() {
        client.close();
        client = null;
    }

    public void parseCommand(String line) {
        if (line.length() == 0)
            return;
        String[] args = line.split(" ");
        if (line.charAt(0) == '/') {
            switch (args[0]) {
                case Command.QUIT: stop();
                    break;
                case Command.CALL:
                    break;
                case Command.ANSWER:
                    break;
                case Command.HANGUP:
                    break;
                case Command.TOGGLE_DEBUG: toggleDebug();
                    break;
                default:
                    if (debugEnabled) {
                        switch (args[0]) {
                            case Debug.CONNECT: connect(args);
                                break;
                            case Debug.DISCONNECT: disconnect();
                                break;
                            case Debug.CLIENT_SEND:
                            default:
                        }
                    } else {

                    }
            }
        }
    }
}
