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

    public State sendInvite(String name, int port) {
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


    public void recievedBye() throws IOException {
        throw new IllegalStateException();
    }

    public State sendBye() {
        return getError("sendBye");
    }

    public void recievedByeAck() {
        throw new IllegalStateException();
    }

    public void sendByeAck() {
        throw new IllegalStateException();
    }

}
