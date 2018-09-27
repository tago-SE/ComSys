package States;

import Net.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class State {

    public Server server;
    public Client client;

    public void connect(Socket socket) {
        throw new IllegalStateException();
    }

    public boolean isBusy() {
        throw new IllegalStateException();
    }

    public void hangup() {
        throw new IllegalStateException();
    }

    public void error() {
        throw new IllegalStateException();
    }

    public void recievedInvite() throws IOException {
        throw new IllegalStateException();
    }

    public void sendInvite(String name, int port) throws IOException {
        throw new IllegalStateException();
    }

    public void recievedTRO() throws IOException {
        throw new IllegalStateException();
    }

    public void sendTRO() throws IOException {
        throw new IllegalStateException();
    }

    public void recievedTROAck() {
        throw new IllegalStateException();
    }

    public void sendTROAck() throws IOException {
        throw new IllegalStateException();
    }

    public void recievedBye() throws IOException {
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
