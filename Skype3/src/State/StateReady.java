package State;

import Handler.StateHandler;
import Net.*;

import java.io.IOException;

public class StateReady extends State {

    private StateHandler handler;
    private Client client;
    private Server server;

    public StateReady() {
        handler = StateHandler.getInstance();
        server = handler.getServer();
        client = handler.getClient();
        if (client != null) {
            client.close();
            client = null;
        }
        if (server != null)
            server.dropClient();
    }

    @Override
    public synchronized State sendInvite(String name, int port)  {
        if (client != null) {
            System.err.println("Invite has already been sent previously.");
            return new StateReady();
        }
        if (server.getPort() == port) {
            System.err.println("Cannot call peer on same port.");
            return new StateReady();
        }
        client = new Client(handler);
        try {
            client.connect(name, port);
            handler.setClient(client);
            client.write(Protocol.INVITE);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new StateReady();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("SendInvite - Argument error.");
            return new StateReady();
        }
        return new StateCalling();
    }

    public synchronized State recievedInvite() {
        return new StateRinging();
    }

}
