package Handler;

import Phone.State;

public class StateHandler {

    private State state;
    private boolean quitSignal;

    public StateHandler(State state) {
        this.state = state;
        quitSignal = false;
    }

    public boolean isBusy() {
        return state.isBusy();
    }

    public boolean shouldQuit() {
        return quitSignal;
    }

    public void parseCommand(String line) {
        if (line.length() == 0)
            return;
        String[] args = line.split(" ");
        if (line.charAt(0) == '/') {
            switch (args[0]) {
                case Command.QUIT: {
                    System.out.println("Quitting program...");
                    quitSignal = true;
                } break;
                case Command.CALL: {
                    System.out.println("Calling...");
                } break;
                case Command.ANSWER: {
                    System.out.println("Answering...");
                } break;
                case Command.HANGUP: {
                    System.out.println("Hanging up...");
                } break;
                default: {
                    System.err.println("Invalid Command.");
                }
            }
        }
    }

    public void parseProtocolDataUnit(String line) {
        switch(line) {
            case Protocol.INVITE: {

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
            }
        }
    }
}
