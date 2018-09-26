package Handler;

import Net.Protocol;
import States.State;
import States.StateCalling;
import States.StateReady;

import java.io.IOException;

public class StateHandler {
    private static StateHandler ourInstance = new StateHandler();

    public static StateHandler getInstance() {
        return ourInstance;
    }

    private State state = new StateReady();
    private boolean quitSignal = false;

    public State getState() {
        return state;
    }

    public boolean shouldQuit() {       // Terminating state...?
        return quitSignal;
    }

    private StateHandler() {
    }

    public void setState(State state) {
        System.out.println("State changed: " + state.getClass().getSimpleName());
        this.state = state;
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
                        System.out.println("Answering...");
                    }
                    break;
                    case Command.HANGUP: {
                        System.out.println("Hanging up...");
                    }
                    break;
                    default: {
                        System.err.println("Invalid Command.");
                    }
                }
            } catch (IOException ioe) {
                System.err.println("IO: " + ioe.getMessage());
            } catch (IllegalStateException ise) {
                System.err.println("State: " + ise.getMessage());
            }
        }
    }

    public synchronized void parseProtocolDataUnit(String line) {
        switch(line) {
            case Protocol.INVITE: {
                state.recievedInvite();
            } break;
            case Protocol.TRO: {

            } break;
            case Protocol.TRO_ACK: {

            } break;
            case Protocol.BYE: {

            } break;
            case Protocol.OK: {

            } break;
            default: {
                System.err.println("Invalid ProtocolDataUnit.");
                setState(new StateReady());
            }
        }
    }
}
