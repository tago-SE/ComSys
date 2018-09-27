package States;

import Net.Protocol;

import java.io.IOException;


public class StateRinging extends StateBusy {

     @Override
    public void sendTRO() throws IOException {
        server.write(Protocol.TRO);
    }

    @Override
    public void recievedTROAck() {
        System.out.println("Answered");
    }

}
