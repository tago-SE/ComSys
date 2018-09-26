package States;

import Net.Protocol;
import Net.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;

public class StateReady extends State {

    public StateReady() {
        if (server != null) {
            server.drop();  // Drop any previously connected peer
        }
    }

    @Override
    public boolean isBusy() {
        return false;
    }


    private boolean callingSelf(String name, int port) throws IOException {
        return (name.equals("localhost") || InetAddress.getLocalHost().getHostAddress().equals(name))
                && server.port == port;
    }

    @Override
    public void sendInvite(String name, int port) throws IOException, IllegalArgumentException {
            if (callingSelf(name, port)) {
                System.err.println("Cannot call self.");
                return;
            }
            System.out.println("sending invite...");
            client = new Client(name, port);
            client.write(Protocol.INVITE);
            //client.start();
    }

    public void recievedInvite() {
        System.out.println("recv invite...");
    }

}
