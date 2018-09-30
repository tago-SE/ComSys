package State;
import Handler.*;
import Net.*;

import java.io.IOException;

public class StateCalling extends State {

    private StateHandler handler = StateHandler.getInstance();
    private Client client;

    public StateCalling() {
        client = handler.client;
    }

    @Override
    public synchronized State recievedTRO() {
        try {
            client.write(Protocol.TRO_ACK);
        } catch (IOException e) {
            System.err.println(e.getMessage());

            return new StateReady();
        }
        return new StateSpeaking();
    }

    @Override
    public synchronized State hangup() {
        return new StateReady();
    }
}
