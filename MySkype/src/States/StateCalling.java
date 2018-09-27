package States;

import Net.Protocol;

import java.io.IOException;

public class StateCalling extends StateBusy {

    @Override
    public void recievedTRO() throws IOException {
        client.write(Protocol.TRO_ACK);
    }
}
