package States;

import Net.Protocol;

import java.io.IOException;

public class StateSpeaking extends StateBusy {

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
