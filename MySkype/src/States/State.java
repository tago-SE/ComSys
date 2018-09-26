package States;

import Net.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class State {

    public Server server;
    public Client client;

    public ServerSocket serverSocket;
    public int serverPort;

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

    public void sendTRO() {
        throw new IllegalStateException();
    }

    public void recievedTROAcknowledge() {

    }

    public void sendTROAcknowledge() {

    }

}
