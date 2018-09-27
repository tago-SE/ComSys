package States;

import Net.*;

import java.io.IOException;
import java.net.InetAddress;

public class StateReady extends State {

    public StateReady(State prev) {
        if (prev == null)
            return;
        this.server = prev.server;
        this.client = prev.client;
        if (this.server != null)
            this.server.drop();
        if (this.client != null)
            this.client.close();
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    public void connect() {

    }

    private boolean callingSelf(String name, int port) throws IOException {
        return (name.equals("localhost") || InetAddress.getLocalHost().getHostAddress().equals(name))
                && server.port == port;
    }

    @Override
    public void sendInvite(String name, int port) throws IOException, IllegalArgumentException {
        if (callingSelf(name, port)) {
            throw new IllegalStateException("Cannot call self");
        }
        client = new Client(name, port);
        client.write(Protocol.INVITE);
    }

    public void recievedInvite() throws IOException {
        System.out.println("Invite recieved");
    }

}
