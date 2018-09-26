package States;

import Net.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class State {

    public Server server;
    public Client client;

    public boolean isBusy() {
        throw new IllegalStateException();
    }

    /*
    public void connect(Socket socket) throws IOException {
        throw new IllegalStateException();
    }
    */


    public void error() {
        throw new IllegalStateException();
    }

    public void recievedInvite() {
        throw new IllegalStateException();
    }

    public void sendInvite(String name, int port) throws IOException {
        throw new IllegalStateException();
    }

    public void recievedTRO() {
        throw new IllegalStateException();
    }

    public void sendTRO() throws IOException {
        throw new IllegalStateException();
    }

    public void recievedTROAck() {
        throw new IllegalStateException();
    }

    public void sendTROAck() {
        throw new IllegalStateException();
    }

    public void recievedBye() {
        throw new IllegalStateException();
    }

    public void sendBye() throws IOException {
        throw new IllegalStateException();
    }

    public void recievedByeAck() {
        throw new IllegalStateException();
    }

    public void sendByeAck() {
        throw new IllegalStateException();
    }

}
