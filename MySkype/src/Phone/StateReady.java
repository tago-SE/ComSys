package Phone;

import java.net.Socket;

public class StateReady extends PhoneState {

    @Override
    public boolean isBusy() {
        return false;
    }

    public void sendInvite() {

    }

    

    public void inviteReceived() {

    }
}
