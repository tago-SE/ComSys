package State;
import Handler.*;
import Net.*;

import java.io.IOException;
import java.net.InetAddress;

public class StateCalling extends State {

    private final Client client;
    private final AudioStreamUDP audio;

    public StateCalling() {
        StateHandler handler = StateHandler.getInstance();
        client = handler.getClient();
        audio = handler.getAudioStreamUDP();
    }

    public synchronized State recievedTRO(int port) {
        try {
            String address = client.socket.getInetAddress().getHostAddress();
            System.out.println("Audio connecting to: " + address + ":" + port);
            audio.connectTo(InetAddress.getByName(address), port);
        } catch (IOException e) {
            e.printStackTrace();
            return new StateReady();
        }
        try {
            client.write(Protocol.TRO_ACK);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new StateReady();
        }
        return new StateSpeaking();
    }

    @Override
    public synchronized State hangup() {
        return new StateReady();
    }
}
