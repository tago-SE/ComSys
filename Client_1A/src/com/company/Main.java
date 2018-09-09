package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        String sendHello, receivedResponse, gameInput;
        boolean run;
        DatagramSocket socket = null;

        // get a datagram socket
        try {
            socket = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        try {

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


                socket.receive(packet);

            receivedResponse = new String(packet.getData(), 0, packet.getLength());
            if (receivedResponse.equals("OK")) {
                run = true;
                System.out.println("Run set to true");
            } else if (receivedResponse.equals("BUSY")) {
                System.exit(1);
            }

        } catch (IOException ioExc){

            ioExc.printStackTrace();
            System.exit(1);
        }
        finally{

            try{
                socket.close();
            } catch (Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
