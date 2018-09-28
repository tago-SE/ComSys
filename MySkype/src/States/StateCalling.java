package States;

import Handler.StateHandler;
import Net.Client;
import Net.Protocol;

import java.io.IOException;

public class StateCalling extends StateBusy {

    private StateHandler handler = StateHandler.getInstance();
    private Client client = handler.client;

    @Override
    public void recievedTRO() throws IOException {
        client.write(Protocol.TRO_ACK);
        //StateHandler.getInstance().setState(new Sta)
    }
}
