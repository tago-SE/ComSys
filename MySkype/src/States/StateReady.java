package States;

import Handler.StateHandler;
import Net.*;

import java.io.IOException;
import java.net.InetAddress;

public class StateReady extends State {

    private StateHandler handler = StateHandler.getInstance();
    private Server server = handler.server;
    private Client client = handler.client;

    public StateReady(State prev) {
        if (prev == null)
            return;
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
        client.setTimeout(10000);
        client.write(Protocol.INVITE);
    }

    public void recievedInvite() throws IOException {
        System.out.println("Invite recieved");
    }

}
