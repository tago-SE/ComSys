package State;

import Handler.StateHandler;
import Net.Client;
import Net.Protocol;
import Net.Server;

import java.io.IOException;

public class StateRinging extends State {

    private StateHandler handler = StateHandler.getInstance();

    @Override
    public synchronized State hangup() {
        return new StateReady();
    }

    @Override
    public synchronized State recievedTROAck() {
        return new StateSpeaking();
    }
}
