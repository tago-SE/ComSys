package State;

import Handler.StateHandler;
import Net.Client;
import Net.Protocol;
import Net.Server;

import java.io.IOException;

public class StateRinging extends State {

    private StateHandler handler = StateHandler.getInstance();
    private Server server = handler.server;

    @Override
    public synchronized State sendTRO() {
        try {
            server.write(Protocol.TRO);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new StateReady();
        }
        return new StateRinging();
    }

    @Override
    public synchronized State hangup() {
        return new StateReady();
    }

    @Override
    public synchronized State recievedTROAck() {
        return new StateSpeaking();
    }
}
