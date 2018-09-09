/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;

public class Main {

    static class Host {
        int port;
        InetAddress address;
        long time;

        public Host(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.time = System.currentTimeMillis();
        }
    }

    public static DatagramPacket createPacket(InetAddress ip, int port, String msg) throws UnsupportedEncodingException {
        byte[] data = msg.getBytes("UTF-8");
        return new DatagramPacket(data, data.length,ip, port);
    }



    public static void main(String[] args) {
        if (args.length != 1 || !args[0].matches("[a-zA-Z]+")){
            (new IllegalArgumentException()).printStackTrace();
            System.exit(1);
        }
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException e) {
            System.exit(1);
        }

        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        boolean run = true;
        Host curr;
        Host prev = null;
        int guessCount = 0;
        int guessMax = args[0].length();
        String secret = args[0];
        boolean waitingForHello = true;

        try {
            while (run) {

                socket.receive(packet);


                // Manage connections - one client at a time

                curr = new Host(packet.getAddress(), packet.getPort());

                if (prev != null && prev.address != curr.address) {

                    if (curr.time < prev.time + 10.000) {

                        socket.send(createPacket(curr.address, curr.port, "BUSY"));

                        continue; // Ignore client
                    } else {
                        // Terminate previous session if a new connection is
                        // accepted and the timer has expired
                        socket.send(createPacket(prev.address, prev.port, "ERROR 1"));
                        waitingForHello = true;
                    }
                }

                String recv = new String(packet.getData(), 0, packet.getLength());

                if (waitingForHello) {
                    if (recv.equals("HELLO")) {
                        waitingForHello = false;
                        socket.send(createPacket(curr.address, curr.port, "OK"));
                    } else {
                        socket.send(createPacket(curr.address, curr.port, "ERROR 2"));
                    }
                } else {
                    if (recv.equals("START")) {
                        socket.send(createPacket(curr.address, curr.port, "READY" + secret.length()));
                        guessCount = 0;
                    } else if (recv.substring(0, 5).equals("GUESS")) {
                        System.out.println("GUESS RECIEVED");
                    } else {
                        socket.send(createPacket(curr.address, curr.port, "ERROR 3"));
                    }
                }
                prev = curr;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            run = false;
        }
        finally{

            try{
                socket.close();
            }catch (Exception ex){
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}