package States;

import Handler.StateHandler;
import Net.Client;
import Net.Protocol;
import Net.Server;

import java.io.IOException;

public class StateSpeaking extends StateBusy {

    private StateHandler handler = StateHandler.getInstance();
    private Server server = handler.server;
    private Client client = handler.client;

    public StateSpeaking() throws IOException {
        if (client != null) client.setTimeout(0);
    }

    @Override
    public void sendBye() throws IOException {
        if (server.hasConnection()) {
            server.write(Protocol.BYE);
        }
        else {
            client.write(Protocol.BYE);
        }
    }

    @Override
    public void recievedBye() throws IOException {
        if (server.hasConnection()) {
            server.write(Protocol.OK);
        }
        else {
            client.write(Protocol.OK);
        }
    }
}
