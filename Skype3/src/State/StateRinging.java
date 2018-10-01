package State;

import Handler.StateHandler;
import Net.AudioStreamUDP;
import Net.Client;
import Net.Protocol;
import Net.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class StateRinging extends State {

    private final StateHandler handler;
    private final Server server;
    private final AudioStreamUDP audio;

    public StateRinging() {
        handler = StateHandler.getInstance();
        server = handler.getServer();
        audio = handler.getAudioStreamUDP();
    }
    @Override
    public synchronized State hangup() {
        return new StateReady();
    }

    public synchronized State sendTRO() {
        try {
            server.write(Protocol.TRO + " " + audio.getLocalPort());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new StateReady();
        }
        try {

            String address = server.getClientSocket().getInetAddress().getHostAddress();
            int port = handler.remoteAudioPort;
            System.out.println("Audio connecting to: " + address + ":" + port);
            audio.connectTo(InetAddress.getByName(address), port);
            // audio.connectTo(server.getClientSocket().getInetAddress(), port);
        } catch (IOException e) {
            e.printStackTrace();
            return new StateReady();
        }
        return new StateRinging();
    }


    @Override
    public synchronized State recievedTROAck() {
        try {
            server.setTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return new StateSpeaking();
    }
}
