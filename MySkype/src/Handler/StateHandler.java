package Handler;

import Net.Protocol;
import States.*;

import java.io.IOException;

public class StateHandler {
    private static StateHandler ourInstance = new StateHandler();

    public static StateHandler getInstance() {
        return ourInstance;
    }

    private State state = new StateReady(null);
    private boolean quitSignal = false;

    public synchronized State getState() {
        return state;
    }

    public boolean shouldQuit() {       // Terminating state...?
        return quitSignal;
    }


    private StateHandler() {
    }

    public synchronized void setState(State state) {
        state.client =  this.state.client;
        state.server = this.state.server;
        System.out.println("State changed: " + state.getClass().getSimpleName());
        this.state = state;
    }

    public synchronized void error() {
        setState(new StateReady(state));
    }

    public synchronized void shutdown() {
        quitSignal = true;
    }

    public synchronized void parseCommand(String line) {
        if (line.length() == 0)
            return;
        String[] args = line.split(" ");
        if (line.charAt(0) == '/') {
            try {
                switch (args[0]) {
                    case Command.QUIT: {
                        System.out.println("Quitting program...");
                        quitSignal = true;
                    }
                    break;
                    case Command.CALL: {
                        try {
                            state.sendInvite(args[1], Integer.parseInt(args[2]));
                            setState(new StateCalling());
                        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException  e) {
                            System.err.println("Invalid call entry.");
                        }
                    }
                    break;
                    case Command.ANSWER: {
                        state.sendTRO();
                    }
                    break;
                    case Command.HANGUP: {
                        System.out.println("Hanging up...");
                        state.sendBye();
                        setState(new StateHangingUp());
                    }
                    break;
                    default: {
                        System.err.println("Invalid Command.");
                    }
                }
            } catch (IllegalStateException | IOException ioe) {
                System.err.println("Command failure.");
                setState(new StateReady(state));
            }
        }
    }

    public synchronized void parseProtocolDataUnit(String line) {
        try {
            switch (line) {
                case Protocol.INVITE: {
                    state.recievedInvite();
                    setState(new StateRinging());
                } break;
                case Protocol.TRO: {
                    state.recievedTRO();
                    setState(new StateSpeaking());
                } break;
                case Protocol.TRO_ACK: {
                    state.recievedTROAck();
                    setState(new StateSpeaking());
                }break;
                case Protocol.BYE: {
                    state.recievedBye();
                    setState(new StateReady(state));
                } break;
                case Protocol.OK: {
                    state.recievedByeAck();
                    setState(new StateReady(state));
                } break;
                default: {
                    System.err.println("ProtocolDataUnit failure.");
                    setState(new StateReady(state));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setState(new StateReady(state));
        }
    }
}
