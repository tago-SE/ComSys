package State;

import Handler.StateHandler;
import Net.*;

import java.io.IOException;

public class StateSpeaking extends State {

    private static final int HANGUP_TIME = 150;

    private StateHandler handler = StateHandler.getInstance();
    private Server server = handler.getServer();
    private Client client = handler.getClient();
    private AudioStreamUDP audio = handler.getAudioStreamUDP();

    public StateSpeaking() {
        audio.startStreaming();
        server.setTimeout(0);
        try {
            if (client != null)
                client.setTimeout(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized State hangup() {
        if (client != null) try {
            client.write(Protocol.BYE);
        } catch (IOException e) {
            System.err.println("Client: " + e.getMessage());
            return new StateReady();
        }
        if (server.hasConnection()) try {
            server.write(Protocol.BYE);
        } catch (IOException e) {
            System.err.println("Server: " + e.getMessage());
            return new StateReady();
        }
        return new StateHangingUp();
    }

    public synchronized State recievedBye() {
        if (client != null) try {
            client.write(Protocol.OK);
        } catch (IOException e) {
            System.err.println("Client: " + e.getMessage());
        }
        if (server.hasConnection()) try {
            server.write(Protocol.OK);
        } catch (IOException e) {
            System.err.println("Server: " + e.getMessage());
        }
        try {
            Thread.sleep(HANGUP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new StateReady();
    }
}
