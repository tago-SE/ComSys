package State;
import Handler.*;
import Net.*;

import java.io.IOException;

public class StateCalling extends State {

    private final Client client;

    public StateCalling() {
        client = StateHandler.getInstance().getClient();
    }

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
