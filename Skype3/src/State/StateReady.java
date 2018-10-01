package State;

import Handler.StateHandler;
import Net.*;

import java.io.IOException;
import java.net.SocketException;

public class StateReady extends State {

    private StateHandler handler;
    private Client client;
    private Server server;
    private AudioStreamUDP audio;

    private static final int TIME_OUT = 5000;

    public StateReady() {
        handler = StateHandler.getInstance();
        server = handler.getServer();
        client = handler.getClient();               // Deadlock risk?
        audio = handler.getAudioStreamUDP();
        handler.remoteAudioPort = 0;
        if (client != null) {
            client.close();
            client = null;
        }
        if (server != null)
            server.dropClient();

        audio.stopStreaming();
    }

    @Override
    public synchronized State sendInvite(String name, String portString)  {
        if (client != null) {
            System.err.println("Invite has already been sent previously.");
            return new StateReady();
        }
        int port = 0;
        try {
            port = Integer.parseInt(portString);
            if (server.getPort() == port) {
                System.err.println("Cannot call peer on same port.");
                return new StateReady();
            }
        } catch (Exception e) {
            System.err.println("Invalid argument for port number.");
            return new StateReady();
        }
        client = new Client(handler);
        try {
            client.connect(name, port);
            handler.setClient(client);
            client.setTimeout(TIME_OUT);
            client.write(Protocol.INVITE + " " + handler.getAudioStreamUDP().getLocalPort());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new StateReady();
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.println("SendInvite - Argument error.");
            return new StateReady();
        }
        return new StateCalling();
    }

    public synchronized State recievedInvite(int localPort) {
        handler.remoteAudioPort = localPort;
        try {
            server.setTimeout(TIME_OUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return new StateRinging();
    }

}
