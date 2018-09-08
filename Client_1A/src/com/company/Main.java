package com.company;

import java.io.IOException;
import java.net.*;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("testing with gitignore");
        // get a datagram socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        // send request
        byte[] buffer = new byte[256];
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);

        socket.send(packet);


        // get response
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}
