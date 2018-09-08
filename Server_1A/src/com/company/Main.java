package com.company;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.net.*;
import java.util.Date;

public class Main {

    static class Host {
        int port;
        InetAddress address;

        public Host(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public String toString() {
            return address.getHostAddress() + ":" + port;
        }
    }

    /*
    static void response(Socket socket, String arg){
        String sendString = "polo";
        byte[] sendData = new byte[0];
        try {
            sendData = sendString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        socket.send(sendPacket);
    }
    */

    static void response(Socket socket, Host host, String s) {

    }

    public static void main(String[] args) {
        Host server = null;
        DatagramSocket socket = null;
        try {
            server =  new Host(InetAddress.getLocalHost(), 4445);
            socket = new DatagramSocket(server.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        byte[] buffer = new byte[256];
        DatagramPacket recievePacket = new DatagramPacket(buffer, buffer.length);

        boolean run = true;
        System.out.println("Listening on udp:" + server);

        Host client = null;

        while (run) {
            try {
                socket.receive(recievePacket);
                Host newHost = new Host(recievePacket.getAddress(), recievePacket.getPort());
                // Block multiple connections
                if (client != null && newHost.address != client.address) {

                }
                client = newHost;


                String recv = new String(recievePacket.getData(), 0, recievePacket.getLength());
                System.out.println("RECEIVED: " + recv);

                String send = "polo";
                byte[] sendData = send.getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.address, client.port);
                socket.send(sendPacket);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
               run = false;
            }
        }
        if (socket != null)
            socket.close();

    }

}
