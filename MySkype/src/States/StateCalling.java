package States;

import Net.Protocol;

import java.io.IOException;

public class StateCalling extends StateBusy {

    @Override
    public void recievedTRO() {
        System.out.println("recv TRO");
    }

    @Override
    public void sendTROAck() {
        client.write(Protocol.TRO_ACK);
    }

}
