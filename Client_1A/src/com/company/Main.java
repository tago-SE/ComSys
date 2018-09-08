package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        String sendHello = null;

        // get a datagram socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        socket.setSoTimeout(3000);
        // send request
        sendHello = scan.nextLine();
        byte[] buffer = sendHello.getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);

        socket.send(packet);

        // get response
        byte[] response = new byte[4096];
        packet = new DatagramPacket(response, response.length);


        try{
            socket.receive(packet);
        }
        catch(SocketTimeoutException stoEx){

            System.out.println("Server connection timed out, exiting.");
            System.exit(1);
        }


        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}
