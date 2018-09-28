package States;

import Handler.StateHandler;
import Net.Client;
import Net.Protocol;
import Net.Server;

import java.io.IOException;


public class StateRinging extends StateBusy {

    private StateHandler handler = StateHandler.getInstance();
    private Server server = handler.server;

     @Override
    public void sendTRO() throws IOException {
        server.write(Protocol.TRO);
    }

    @Override
    public void recievedTROAck() {
        System.out.println("Answered");
    }

}
