package State;

import Net.*;

import java.io.IOException;
import java.net.Socket;

public abstract class State {



    private State getError(String msg) {
        System.err.println(this.getClass().getSimpleName() + ": IllegalStateException " + msg);
        return new StateReady();
    }

    public State recievedInvite() {
       return getError("recievedInvite");
    }

    public State sendInvite(String name, String port) {
        return getError("sendInvite");
    }

    public State recievedTRO() {
        return getError("recievedTRO");
    }


    public State sendTRO() {
        return getError("sendTRO");
    }

    public State recievedTROAck() {
        return getError("recievedTROAck");
    }

    public State sendTROAck() {
        return getError("sendTROAck");
    }


    public State hangup() {
        return getError("hangup");
    }

    public void error() {
        throw new IllegalStateException();
    }


    public State recievedBye() throws IOException {
        return getError("recievedBye");
    }

    public State sendBye() {
        return getError("sendBye");
    }

    public State recievedByeAck() {
        return getError("recievedByeAck");
    }

    public void sendByeAck() {
        throw new IllegalStateException();
    }

}
