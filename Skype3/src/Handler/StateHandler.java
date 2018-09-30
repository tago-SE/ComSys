package Handler;

import Net.Client;
import Net.Protocol;
import Net.Server;
import State.*;

import java.io.IOException;

public class StateHandler {

    private static StateHandler ourInstance = new StateHandler();
    private State state;
    private boolean debugEnabled = false;

    private UserInput userInput;
    private Server server;
    private Client client;

    public synchronized Client getClient() {
        return client;
    }

    public synchronized void setClient(Client client) {
        this.client = client;
    }

    public synchronized Server getServer() {
        return server;
    }

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
            if (userInput != null) userInput.close();
            if (server != null) server.close();
            if (client != null) client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public synchronized void setState(State state) {
        this.state = state;
        System.out.println(state.getClass().getSimpleName());
    }

    public synchronized State getState() {
        return state;
    }

    private void parseDebugCommands(String[] args) {

    }

    // Debug
    private void toggleDebug() {
        debugEnabled = !debugEnabled;
        System.out.println("Debug = " + debugEnabled);
    }

    // Debug
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
        } catch (ArrayIndexOutOfBoundsException bounds) {
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
             if (client != null)
                 client.close();
            client = null;
        }
    }

    // Debug
    private synchronized void disconnect() {
        if (client != null) {
            client.close();
            client = null;
        }
        if (server != null)
            server.dropClient();
    }

    // Debug
    private synchronized void clientSend(String[] args) {
        if (args.length < 1)
            return;
        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }
        try {
            client.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("No currently connected client.");
        }
    }

    public synchronized void error() {
        setState(new StateReady());
    }

    public void parseCommand(String line) {
        if (line.length() == 0)
            return;
        try {
            String[] args = line.split(" ");
            if (line.charAt(0) == '/') {
                switch (args[0]) {
                    case Command.QUIT:
                        stop();
                        break;
                    case Command.CALL:
                        setState(state.sendInvite(args[1], Integer.parseInt(args[2])));
                        break;
                    case Command.ANSWER:
                        setState(state.sendTRO());
                        break;
                    case Command.HANGUP:
                        setState(state.hangup());
                        break;
                    case Command.TOGGLE_DEBUG:
                        toggleDebug();
                        break;
                    default:
                        if (debugEnabled) {
                            switch (args[0]) {
                                case Debug.CONNECT:
                                    connect(args);
                                    break;
                                case Debug.DISCONNECT:
                                    disconnect();
                                    break;
                                case Debug.CLIENT_SEND:
                                    clientSend(args);
                                    break;
                                case Debug.SERVER_SEND:
                                    break;
                                default:
                                    System.err.println("Invalid Command.");
                            }
                        } else {
                            System.err.println("Invalid Command.");
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setState(new StateReady());
        }
    }

    public synchronized void parseProtocolDataUnit(String line) {
        try {
            switch (line) {
                case Protocol.INVITE: {
                    setState(state.recievedInvite());   // need to pass argument for invite
                } break;
                case Protocol.TRO: {
                    setState(state.recievedTRO());
                } break;
                case Protocol.TRO_ACK: {
                    setState(state.recievedTROAck());
                }break;
                case Protocol.BYE: {
                    setState(state.recievedBye());
                } break;
                case Protocol.OK: {
                    setState(state.recievedByeAck());
                } break;
                default: {
                    System.err.println("ProtocolDataUnit failure.");
                    setState(new StateReady());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setState(new StateReady());
        }
    }


}
